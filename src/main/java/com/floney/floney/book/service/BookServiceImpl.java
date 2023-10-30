package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.entity.Alarm;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.entity.Budget;
import com.floney.floney.book.entity.category.BookCategory;
import com.floney.floney.book.repository.*;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.book.*;
import com.floney.floney.common.exception.common.NotSubscribeException;
import com.floney.floney.settlement.repository.SettlementRepository;
import com.floney.floney.user.dto.response.AlarmResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Subscribe.DEFAULT_MAX_BOOK;
import static com.floney.floney.common.constant.Subscribe.SUBSCRIBE_MAX_BOOK;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final int ONLY_OWNER_COUNT = 1;
    private static final long DEFAULT_BUDGET = 0L;

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final SettlementRepository settlementRepository;
    private final CarryOverRepository carryOverRepository;
    private final AlarmRepository alarmRepository;

    @Override
    @Transactional
    public CreateBookResponse addBook(User user, CreateBookRequest request) {
        checkCreateBookMaximum(user);
        if (user.isSubscribe()) {
            return subscribeCreateBook(user, request);
        } else {
            return createBook(user, request);
        }
    }

    @Transactional
    public CreateBookResponse subscribeCreateBook(User user, CreateBookRequest request) {
        Book newBook = request.of(user.getEmail());
        Book savedBook = bookRepository.save(newBook);
        savedBook.subscribe(user);

        saveDefaultBookKey(user, savedBook);
        bookUserRepository.save(BookUser.of(user, savedBook));
        return CreateBookResponse.of(savedBook);
    }

    @Override
    @Transactional
    public CreateBookResponse joinWithCode(CustomUserDetails userDetails, CodeJoinRequest request) {
        String code = request.getCode();
        String userEmail = userDetails.getUsername();
        User user = userDetails.getUser();

        Book book = bookRepository.findBookExclusivelyByCodeAndStatus(code, ACTIVE)
                .orElseThrow(() -> new NotFoundBookException(code));

        // 현 유저의 가계부 참여 개수 체크
        checkCreateBookMaximum(user);

        // 참여 희망 가계부 정원 체크
        validateMaxBookCapacity(book);

        // 이미 존재하는 가계부 유저인지 체크
        if (bookUserRepository.findBookUserByCode(userEmail, request.getCode()).isPresent()) {
            throw new AlreadyJoinException(userEmail);
        }

        saveDefaultBookKey(userDetails.getUser(), book);
        bookUserRepository.save(BookUser.of(userDetails.getUser(), book));

        return CreateBookResponse.of(book);
    }

    private void validateMaxBookCapacity(Book book) {
        final int memberCount = bookUserRepository.countByBookExclusively(book);
        book.validateCanJoinMember(memberCount);
    }

    @Override
    @Transactional
    public void changeBookName(BookNameChangeRequest request) {
        Book book = findBook(request.getBookKey());
        book.updateName(request.getName());
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(final String email, final String bookKey) {
        final BookUser bookUser = findBookUserByKey(email, bookKey);

        validateCanDeleteBookBy(bookUser);
        bookUser.inactive();
        deleteBook(bookUser.getBook());
    }

    @Override
    @Transactional(readOnly = true)
    public OurBookInfo getBookInfo(String bookKey, String myEmail) {
        List<OurBookUser> bookUsers = bookUserRepository.findAllUser(bookKey);
        return OurBookInfo.of(findBook(bookKey), bookUsers, myEmail);
    }

    @Override
    @Transactional
    public void updateBookImg(UpdateBookImgRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.updateImg(request);
        bookRepository.save(savedBook);
    }

    @Override
    @Transactional
    public void updateSeeProfile(SeeProfileRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.changeSeeProfile(request.isSeeProfileStatus());
        bookRepository.save(savedBook);
    }

    @Override
    @Transactional
    public void updateCarryOver(CarryOverRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.changeCarryOverStatus(request.isStatus());
        bookRepository.save(savedBook);
    }

    @Override
    @Transactional
    public void saveOrUpdateAsset(UpdateAssetRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.updateAsset(request.getAsset());
        bookRepository.save(savedBook);
    }

    @Override
    @Transactional
    public void saveOrUpdateBudget(UpdateBudgetRequest request) {
        Book savedBook = findBook(request.getBookKey());
        Optional<Budget> savedBudget = budgetRepository.findBudgetByBookAndDate(savedBook, request.getDate());

        if (savedBudget.isPresent()) {
            updateBudget(savedBudget.get(), request);
        } else {
            Budget newBudget = Budget.of(savedBook, request);
            budgetRepository.save(newBudget);
        }
    }

    @Override
    @Transactional(readOnly = true)
    // TODO: '마지막'으로 참여한 내용이 포함되도록 용어 수정
    public InvolveBookResponse findInvolveBook(User user) {
        String recentBookKey = user.getRecentBookKey();
        Optional<Book> book = bookRepository.findBookExclusivelyByBookKeyAndStatus(recentBookKey, ACTIVE);
        return InvolveBookResponse.of(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookUserResponse> findUsersByBook(final CustomUserDetails userDetails, final String bookKey) {
        findBookUserByKey(userDetails.getUsername(), bookKey);

        final List<User> users = new ArrayList<>(List.of(userDetails.getUser()));
        users.addAll(findAllByBookAndStatus(bookKey)
                .stream()
                .map(BookUser::getUser)
                .filter(user -> !user.getEmail().equals(userDetails.getUsername()))
                .toList());

        return userToResponse(users);
    }

    @Override
    @Transactional
    public CurrencyResponse changeCurrency(ChangeCurrencyRequest request) {
        Book book = findBook(request.getBookKey());
        book.changeCurrency(request.getRequestCurrency());
        Book savedBook = makeInitBook(request.getBookKey());
        return CurrencyResponse.of(savedBook);
    }

    @Override
    @Transactional
    public void bookUserOut(BookUserOutRequest request, String userEmail) {
        final BookUser bookUser = findBookUserByKey(userEmail, request.getBookKey());
        inactiveAllBy(bookUser);
        bookUser.inactive();
    }

    @Override
    @Transactional(readOnly = true)
    public InviteCodeResponse inviteCode(String bookKey) {
        return new InviteCodeResponse(findBook(bookKey));
    }


    @Override
    @Transactional
    public Book makeInitBook(String bookKey) {
        Book book = findBook(bookKey);
        book.initBook();
        bookLineCategoryRepository.inactiveAllByBookKey(bookKey);

        categoryRepository.findAllCustomCategory(book)
                .stream()
                .map(BookCategory::delete)
                .forEach(categoryRepository::delete);

        settlementRepository.inactiveAllByBookKey(bookKey);
        bookLineRepository.inactiveAllLines(bookKey);
        carryOverRepository.inactiveAllByBookKey(bookKey);

        List<Budget> initBudgets = budgetRepository.findAllByBook(book)
                .stream()
                .map(Budget::initMoney)
                .toList();
        budgetRepository.saveAll(initBudgets);

        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrency(String bookKey) {
        return CurrencyResponse.of(findBook(bookKey));
    }

    @Override
    @Transactional(readOnly = true)
    public BookInfoResponse getBookInfoByCode(final String code) {
        final Book book = bookRepository.findBookByCodeAndStatus(code, ACTIVE)
                .orElseThrow(() -> new NotFoundBookException(code));
        final int memberCount = bookUserRepository.countByBook(book);
        return BookInfoResponse.of(book, memberCount);
    }

    @Override
    @Transactional(readOnly = true)
    public LastSettlementDateResponse getPassedDaysAfterLastSettlementDate(final String userEmail,
                                                                           final String bookKey) {
        final LocalDate lastSettlementDate = findBook(userEmail, bookKey).getLastSettlementDate();
        if (lastSettlementDate == null) {
            return new LastSettlementDateResponse(0);
        }
        final long passedDays = ChronoUnit.DAYS.between(lastSettlementDate, LocalDate.now());
        return new LastSettlementDateResponse(passedDays);
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatusResponse getBookStatus(String bookKey) {
        return BookStatusResponse.of(findBook(bookKey));
    }

    @Override
    public Map<Month, Long> getBudgetByYear(String bookKey, String firstDate) {
        LocalDate date = LocalDate.parse(firstDate);
        Map<Month, Long> monthlyMap = getInitBudgetFrame();

        List<BudgetYearResponse> savedBudget = bookRepository.findBudgetByYear(bookKey, DateFactory.getYearDuration(date));

        for (BudgetYearResponse budget : savedBudget) {
            Month month = budget.getDate().getMonth();
            monthlyMap.replace(month, budget.getMoney());
        }

        return monthlyMap;
    }

    @Override
    @Transactional
    public void saveAlarm(SaveAlarmRequest request) {
        final String bookKey = request.getBookKey();
        final String userEmail = request.getUserEmail();

        final BookUser bookUser = bookUserRepository.findBookUserByEmailAndBookKey(userEmail, bookKey)
                .orElseThrow(() -> new NotFoundBookUserException(bookKey, userEmail));

        final Alarm alarm = Alarm.of(findBook(bookKey), bookUser, request);
        alarmRepository.save(alarm);
    }

    @Override
    @Transactional
    public void updateAlarmReceived(UpdateAlarmReceived request) {
        Alarm alarm = alarmRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundAlarmException(request.getId()));
        alarm.updateReceived(request.isReceived());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlarmResponse> getAlarmByBook(String bookKey, String email) {
        final BookUser bookUser = bookUserRepository.findBookUserByEmailAndBookKey(email, bookKey)
                .orElseThrow(() -> new NotFoundBookUserException(bookKey, email));

        return alarmRepository.findAllByBookAndBookUser(findBook(bookKey), bookUser)
                .stream()
                .map(AlarmResponse::of)
                .toList();
    }

    @Override
    @Transactional
    public void leaveBooksBy(final long userId) {
        final List<BookUser> bookUsers = bookUserRepository.findAllByUserId(userId);
        bookUsers.forEach(bookUser -> {
            final Book book = bookUser.getBook();
            if (canDeleteBookBy(bookUser)) {
                deleteBook(book);
                return;
            }
            // 가계부 탈퇴
            inactiveAllBy(bookUser);
            bookUser.inactive();
            if (bookUser.isOwner()) {
                delegateOwner(book);
            }
        });
    }

    private void delegateOwner(Book book) {
        final Optional<User> subscribersNotBookOwner = bookUserRepository.findRandomBookUserWhoSubscribeExclusively(book);

        // 위임할 유저가 존재할 경우 방장 위임
        if (subscribersNotBookOwner.isPresent()) {
            final User delegatedBookOwner = subscribersNotBookOwner.get();
            book.delegateOwner(delegatedBookOwner);
            return;
        }
        // 위임할 유저가 없으면 가계부 비활성화
        book.inactiveBookStatus();
    }

    private void deleteBook(final Book book) {
        inactiveAllBy(book);
        book.delete();
    }

    private void inactiveAllBy(final Book book) {
        alarmRepository.inactiveAllByBook(book);
        bookLineRepository.inactiveAllByBook(book);
        bookLineCategoryRepository.inactiveAllByBook(book);
        bookUserRepository.inactiveAllByBook(book);
        budgetRepository.inactiveAllByBook(book);
        carryOverRepository.inactiveAllByBook(book);
        categoryRepository.inactiveAllByBook(book);
    }

    private void inactiveAllBy(final BookUser bookUser) {
        alarmRepository.inactiveAllByBookUser(bookUser);
        bookLineRepository.inactiveAllByBookUser(bookUser);
        bookLineCategoryRepository.inactiveAllByBookUser(bookUser);
    }

    private CreateBookResponse createBook(User user, CreateBookRequest request) {
        Book newBook = request.of(user.getEmail());
        Book savedBook = bookRepository.save(newBook);
        saveDefaultBookKey(user, savedBook);

        bookUserRepository.save(BookUser.of(user, savedBook));
        return CreateBookResponse.of(savedBook);
    }

    private boolean canDeleteBookBy(final BookUser bookUser) {
        return !bookUser.isInactive() && bookUserRepository.countByBookExclusively(bookUser.getBook()) == ONLY_OWNER_COUNT;
    }

    private Map<Month, Long> getInitBudgetFrame() {
        Map<Month, Long> monthlyMap = new LinkedHashMap<>();
        for (Month month : Month.values()) {
            monthlyMap.put(month, DEFAULT_BUDGET);
        }
        return monthlyMap;
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
                .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private void validateCanDeleteBookBy(final BookUser bookUser) {
        if (!canDeleteBookBy(bookUser)) {
            throw new CannotDeleteBookException();
        }
    }

    private List<BookUserResponse> userToResponse(final List<User> users) {
        return users.stream()
                .map(BookUserResponse::from)
                .toList();
    }

    private List<BookUser> findAllByBookAndStatus(String bookKey) {
        return bookUserRepository.findAllByBookAndStatus(findBook(bookKey), ACTIVE);
    }

    private BookUser findBookUserByKey(String userEmail, String bookKey) {
        return bookUserRepository.findBookUserByKey(userEmail, bookKey)
                .orElseThrow(() -> new NotFoundBookUserException(bookKey, userEmail));
    }

    private Book findBook(String userEmail, String bookKey) {
        return bookRepository.findByBookUserEmailAndBookKey(userEmail, bookKey)
                .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private void saveDefaultBookKey(User user, Book book) {
        user.saveDefaultBookKey(book.getBookKey());
        userRepository.save(user);
    }

    private void checkCreateBookMaximum(User user) {
        // 유저가 참여중인 가게부 개수
        int currentJoinBook = bookUserRepository.countBookUserByUserAndStatus(user, ACTIVE);

        if (user.isSubscribe()) {
            if (currentJoinBook >= SUBSCRIBE_MAX_BOOK.getValue()) {
                throw new LimitRequestException();
            }
        } else {
            if (currentJoinBook >= DEFAULT_MAX_BOOK.getValue()) {
                throw new NotSubscribeException();
            }
        }
    }

    private void updateBudget(Budget savedBudget, UpdateBudgetRequest request) {
        savedBudget.update(request);
        budgetRepository.save(savedBudget);
    }
}
