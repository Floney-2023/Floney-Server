package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final int SUBSCRIBE_MAX = 2;
    private static final int DEFAULT_MAX = 1;
    private static final int OWNER = 1;

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final SettlementRepository settlementRepository;
    private final CarryOverRepository carryOverRepository;

    @Override
    @Transactional
    public CreateBookResponse createBook(User user, CreateBookRequest request) {
        Book newBook = request.of(user.getEmail());
        Book savedBook = bookRepository.save(newBook);
        saveDefaultBookKey(user, savedBook);

        bookUserRepository.save(BookUser.of(user, savedBook));
        return CreateBookResponse.of(savedBook);
    }

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

        Book book = bookRepository.findBookByCodeAndStatus(code, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(code));

        // 현 유저의 가계부 참여 개수 체크
        checkCreateBookMaximum(user);

        // 참여 희망 가계부 정원 체크
        bookUserRepository.isMax(book);

        // 이미 존재하는 가계부 유저인지 체크
        if (bookUserRepository.findBookUserByCode(userEmail, request.getCode()).isPresent()) {
            throw new AlreadyJoinException(userEmail);
        }

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
    public void deleteBook(String email, String bookKey) {
        Book book = findBook(bookKey);
        isValidToDeleteBook(book, email);

        BookUser bookUser = findBookUserByKey(email, bookKey);
        deleteBookUser(bookUser);

        book.delete();

        bookRepository.save(book);
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
    public InvolveBookResponse findInvolveBook(User user) {
        String recentBookKey = user.getRecentBookKey();
        Optional<Book> book = bookRepository.findBookByBookKeyAndStatus(recentBookKey, ACTIVE);
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
        BookUser bookUser = findBookUserByKey(userEmail, request.getBookKey());
        deleteBookLineBy(bookUser, request.getBookKey());
        deleteBookUser(bookUser);
    }

    @Override
    @Transactional
    public void deleteBookLine(Book bookUserBook, BookUser bookUser) {
        deleteAllLinesByOnly(bookUserBook, bookUser);
        deleteBookUser(bookUser);
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
        bookLineCategoryRepository.deleteBookLineCategory(bookKey);

        categoryRepository.findAllCustomCategory(book).stream()
            .map(BookCategory::delete)
            .forEach(categoryRepository::delete);

        settlementRepository.deleteAllSettlement(bookKey);
        bookLineRepository.deleteAllLines(bookKey);
        carryOverRepository.deleteAllCarryOver(bookKey);

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
    public BookInfoResponse getBookInfoByCode(String code) {
        Book book = bookRepository.findBookByCodeAndStatus(code, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(code));
        long memberCount = bookUserRepository.countBookUser(book);
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

    private Map<Month, Long> getInitBudgetFrame() {
        Map<Month, Long> monthlyMap = new LinkedHashMap<>();
        for (Month month : Month.values()) {
            monthlyMap.put(month, 0L);
        }
        return monthlyMap;
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private void isValidToDeleteBook(Book book, String email) {
        book.isOwner(email);
        long count = bookUserRepository.countBookUser(book);
        if (count > OWNER) {
            throw new CannotDeleteBookException(count);
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

    private void deleteBookUser(BookUser bookUser) {
        bookUser.delete();
        bookUserRepository.save(bookUser);
    }

    private BookUser findBookUserByKey(String userEmail, String bookKey) {
        return bookUserRepository.findBookUserByKey(userEmail, bookKey)
            .orElseThrow(() -> new NotFoundBookUserException(bookKey, userEmail));
    }

    private Book findBook(String userEmail, String bookKey) {
        return bookRepository.findByBookUserEmailAndBookKey(userEmail, bookKey)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private void deleteBookLineBy(BookUser bookUser, String bookKey) {
        bookLineRepository.deleteAllLinesByUser(bookUser, bookKey);
    }

    private void saveDefaultBookKey(User user, Book book) {
        user.saveDefaultBookKey(book.getBookKey());
        userRepository.save(user);
    }

    private void checkCreateBookMaximum(User user) {
        int currentParticipateCount = bookUserRepository.countBookUserByUserAndStatus(user, ACTIVE);

        if (user.isSubscribe()) {
            if (currentParticipateCount >= SUBSCRIBE_MAX) {
                throw new LimitRequestException();
            }
        } else {
            if (currentParticipateCount >= DEFAULT_MAX) {
                throw new NotSubscribeException();
            }
        }
    }

    private void deleteAllLinesByOnly(Book bookUserBook, BookUser bookUser) {
        bookLineRepository.deleteAllLinesByBookAndBookUser(bookUserBook, bookUser);
    }

    private void updateBudget(Budget savedBudget, UpdateBudgetRequest request) {
        savedBudget.update(request);
        budgetRepository.save(savedBudget);
    }
}
