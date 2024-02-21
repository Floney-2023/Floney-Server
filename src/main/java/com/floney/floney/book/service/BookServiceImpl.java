package com.floney.floney.book.service;

import com.floney.floney.alarm.repository.AlarmRepository;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.Budget;
import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.analyze.AssetRepository;
import com.floney.floney.book.repository.analyze.BudgetRepository;
import com.floney.floney.book.repository.analyze.CarryOverRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.category.DefaultSubcategoryRepository;
import com.floney.floney.book.repository.category.SubcategoryRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.*;
import com.floney.floney.settlement.repository.SettlementRepository;
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

import static com.floney.floney.book.domain.BookCapacity.DEFAULT;
import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final int ONLY_OWNER_COUNT = 1;
    private static final double DEFAULT_BUDGET = 0.0;

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final DefaultSubcategoryRepository defaultSubcategoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final SettlementRepository settlementRepository;
    private final CarryOverRepository carryOverRepository;
    private final AlarmRepository alarmRepository;
    private final AssetRepository assetRepository;

    @Override
    @Transactional
    public CreateBookResponse createBook(final User user, final CreateBookRequest request) {
        validateJoinByBookCapacity(user);

        final Book book = request.to(user.getEmail());
        bookRepository.save(book);
        saveDefaultBookKey(user, book);
        saveDefaultCategories(book);

        bookUserRepository.save(BookUser.of(user, book));
        return CreateBookResponse.of(book);
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
        validateJoinByBookCapacity(user);
        // 참여 희망 가계부 정원 체크
        validateJoinByBookUserCapacity(book);
        // 이미 존재하는 가계부 유저인지 체크
        validateAlreadyJoined(request, userEmail);

        saveDefaultBookKey(userDetails.getUser(), book);
        bookUserRepository.save(BookUser.of(userDetails.getUser(), book));

        return CreateBookResponse.of(book);
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
    public void deleteBook(final User user, final String bookKey) {
        final BookUser bookUser = findBookUserByKey(user.getEmail(), bookKey);

        validateCanDeleteBookBy(bookUser);
        bookUser.inactive();
        deleteBook(bookUser.getBook());
        saveAnotherRecentBookKey(user);
        userRepository.save(user);
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
    @Transactional
    // TODO: 좀 더 구체적인 내용이 드러나도록 용어 수정
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
    public CurrencyResponse changeCurrency(final ChangeCurrencyRequest request) {
        final Book book = findBook(request.getBookKey());
        resetBookExceptCategory(book);
        book.changeCurrency(request.getRequestCurrency());
        return CurrencyResponse.of(book);
    }

    @Override
    @Transactional
    public void bookUserOut(final BookUserOutRequest request, final User user) {
        final BookUser bookUser = findBookUserByKey(user.getEmail(), request.getBookKey());
        inactiveAllBy(bookUser);
        bookUser.inactive();
        bookUserRepository.save(bookUser);

        // 유효 가계부 초기화 하기(다른 참여 가계부가 없다면 null로 초기화)
        saveAnotherRecentBookKey(user);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public InviteCodeResponse inviteCode(String bookKey) {
        return new InviteCodeResponse(findBook(bookKey));
    }


    @Override
    @Transactional
    public void resetBook(final String bookKey) {
        final Book book = findBook(bookKey);
        resetBookExceptCategory(book);
        categoryRepository.inactiveAllByBook(book);
        saveDefaultCategories(book);
    }

    private void resetBookExceptCategory(final Book book) {
        book.initBook();

        bookLineRepository.inactiveAllBy(book);
        bookLineCategoryRepository.inactiveAllByBook(book);
        assetRepository.inactiveAllBy(book);
        settlementRepository.inactiveAllBy(book);
        carryOverRepository.inactiveAllBy(book);
        budgetRepository.inactiveAllBy(book);
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
    public Map<Month, Double> getBudgetByYear(String bookKey, String firstDate) {
        LocalDate date = LocalDate.parse(firstDate);
        Map<Month, Double> monthlyMap = getInitBudgetFrame();

        List<BudgetYearResponse> savedBudget = budgetRepository.findBudgetByYear(bookKey, DateDuration.firstAndEndDayOfYear(date));

        for (BudgetYearResponse budget : savedBudget) {
            Month month = budget.getDate().getMonth();
            monthlyMap.replace(month, budget.getMoney());
        }

        return monthlyMap;
    }

    @Override
    @Transactional
    public void leaveBooksBy(final long userId) {
        final List<BookUser> bookUsers = bookUserRepository.findAllByUserId(userId);
        bookUsers.forEach(bookUser -> {
            // 가계부 탈퇴
            inactiveAllBy(bookUser);
            bookUser.inactive();
        });
    }

    private void validateAlreadyJoined(final CodeJoinRequest request, final String userEmail) {
        if (bookUserRepository.findBookUserByCode(userEmail, request.getCode()).isPresent()) {
            throw new AlreadyJoinException(userEmail);
        }
    }

    private void saveAnotherRecentBookKey(User user) {
        List<MyBookInfo> myBookInfos = bookUserRepository.findMyBookInfos(user);
        myBookInfos.stream()
            .findFirst()
            .ifPresentOrElse(
                bookInfo -> user.saveRecentBookKey(bookInfo.getBookKey()),
                () -> user.saveRecentBookKey(null)
            );
    }

    private void validateJoinByBookUserCapacity(Book book) {
        final int memberCount = bookUserRepository.countByBookExclusively(book);
        book.validateCanJoinMember(memberCount);
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
        budgetRepository.inactiveAllBy(book);
        carryOverRepository.inactiveAllByBook(book);
        categoryRepository.inactiveAllByBook(book);
    }

    private void inactiveAllBy(final BookUser bookUser) {
        alarmRepository.inactiveAllByBookUser(bookUser);
        bookLineRepository.findAllByBookUser(bookUser)
            .forEach(BookLine::inactive);
        bookLineCategoryRepository.inactiveAllByBookUser(bookUser);
    }

    private void saveDefaultCategories(final Book book) {
        final List<Subcategory> subcategories = defaultSubcategoryRepository.findAllByStatus(ACTIVE)
            .stream()
            .map(defaultSubcategory -> Subcategory.of(defaultSubcategory, book))
            .toList();

        subcategoryRepository.saveAll(subcategories);
    }

    private boolean canDeleteBookBy(final BookUser bookUser) {
        return !bookUser.isInactive() && bookUserRepository.countByBookExclusively(bookUser.getBook()) == ONLY_OWNER_COUNT;
    }

    private Map<Month, Double> getInitBudgetFrame() {
        Map<Month, Double> monthlyMap = new LinkedHashMap<>();
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
        return bookUserRepository.findBookUserByEmailAndBookKey(userEmail, bookKey)
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

    private void validateJoinByBookCapacity(User user) {
        // 유저가 참여중인 가게부 개수
        int currentJoinBook = bookUserRepository.countBookUserByUserAndStatus(user, ACTIVE);

        // 이미 최대로 가계부들에 참여한 경우
        // TODO: currentJoinBook > DEFAULT_MAX_BOOK.getValue() 이면 서버 에러 발생
        if (currentJoinBook >= DEFAULT.getValue()) {
            throw new LimitRequestException();
        }
    }

    private void updateBudget(Budget savedBudget, UpdateBudgetRequest request) {
        savedBudget.update(request);
        budgetRepository.save(savedBudget);
    }
}
