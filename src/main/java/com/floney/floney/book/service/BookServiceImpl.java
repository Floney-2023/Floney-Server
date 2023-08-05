package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.AnalyzeResponse;
import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.AnalyzeByCategory;
import com.floney.floney.book.dto.response.InvolveBookResponse;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.InviteCodeResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookAnalyze;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookAnalyzeRepository;
import com.floney.floney.book.repository.BookLineCustomRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryCustomRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.LimitRequestException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.common.NotSubscribeException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final int SUBSCRIBE_MAX = 2;
    private static final int DEFAULT_MAX = 1;

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineCustomRepository bookLineRepository;
    private final BookAnalyzeRepository analyzeRepository;
    private final CategoryCustomRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CreateBookResponse createBook(CustomUserDetails userDetails, CreateBookRequest request) {
        Book newBook = request.of(userDetails.getUsername());
        Book savedBook = bookRepository.save(newBook);

        bookUserRepository.save(BookUser.of(userDetails.getUser(), savedBook));
        return CreateBookResponse.of(savedBook);
    }

    @Override
    @Transactional
    public CreateBookResponse addBook(CustomUserDetails userDetails, CreateBookRequest request) {
        User user = userDetails.getUser();
        int count = bookUserRepository.countBookUserByUserAndStatus(user, Status.ACTIVE);
        if (user.isSubscribe()) {
            return subscribeCreateBook(count, userDetails, request);
        } else {
            return notSubscribeCreateBook(count, userDetails, request);
        }
    }

    @Transactional
    public CreateBookResponse subscribeCreateBook(int count, CustomUserDetails userDetails, CreateBookRequest request) {
        if (count >= SUBSCRIBE_MAX) {
            throw new LimitRequestException();
        }
        return createBook(userDetails, request);
    }

    @Transactional
    public CreateBookResponse notSubscribeCreateBook(int count, CustomUserDetails userDetails, CreateBookRequest request) {
        if (count >= DEFAULT_MAX) {
            throw new NotSubscribeException();
        }
        return createBook(userDetails, request);
    }

    @Override
    @Transactional
    public CreateBookResponse joinWithCode(CustomUserDetails userDetails, CodeJoinRequest request) {
        String code = request.getCode();
        Book book = bookRepository.findBookByCodeAndStatus(code, Status.ACTIVE)
            .orElseThrow(NotFoundBookException::new);
        bookUserRepository.isMax(book);
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

    private void isValidToDeleteBook(Book book, String email) {
        book.isOwner(email);
        bookUserRepository.countBookUser(book);
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
    public void updateAsset(UpdateAssetRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.updateAsset(request.getAsset());
        bookRepository.save(savedBook);
    }

    @Override
    @Transactional
    public void updateBudget(UpdateBudgetRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.updateBudget(request.getBudget());
        bookRepository.save(savedBook);
    }

    @Override
    public InvolveBookResponse findInvolveBook(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);
        return InvolveBookResponse.of(user.getRecentBookKey());
    }

    @Override
    @Transactional(readOnly = true)
    public Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE).orElseThrow(NotFoundBookException::new);
    }

    @Override
    @Transactional
    public void updateLastSettlementDate(String bookKey, LocalDate settlementDate) {
        final Book book = findBook(bookKey);
        book.updateLastSettlementDate(settlementDate);
        bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findUsersByBookExceptCurrentUser(CustomUserDetails userDetails, String bookKey) {
        return bookUserRepository.findAllByBookAndStatus(findBook(bookKey), Status.ACTIVE)
            .stream()
            .map(bookUser -> UserResponse.from(bookUser.getUser()))
            .filter(user -> !user.getEmail().equals(userDetails.getUsername()))
            .toList();
    }

    @Override
    @Transactional
    public void bookUserOut(BookUserOutRequest request, String userEmail) {
        BookUser bookUser = findBookUserByKey(userEmail, request.getBookKey());
        deleteBookLineBy(bookUser, request.getBookKey());
        deleteBookUser(bookUser);
    }

    private BookUser deleteBookUser(BookUser bookUser) {
        bookUser.delete();
        return bookUserRepository.save(bookUser);
    }

    private BookUser findBookUserByKey(String userEmail, String bookKey) {
        return bookUserRepository.findBookUserByKey(userEmail, bookKey)
            .orElseThrow(NotFoundBookUserException::new);
    }

    private void deleteBookLineBy(BookUser bookUser, String bookKey) {
        bookLineRepository.deleteAllLinesByUser(bookUser, bookKey);
    }

    @Override
    @Transactional(readOnly = true)
    public InviteCodeResponse inviteCode(String bookKey) {
        return new InviteCodeResponse(findBook(bookKey));
    }

    @Override
    @Transactional
    public AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request) {
        List<AnalyzeByCategory> analyzeResultByCategory = bookLineRepository.analyzeByCategory(request);
        BookAnalyze savedAnalyze = saveAnalyze(request, analyzeResultByCategory);

        return AnalyzeResponse.of(analyzeResultByCategory, savedAnalyze,
            calculateDifference(request, savedAnalyze));
    }

    private Long calculateDifference(AnalyzeByCategoryRequest request, BookAnalyze currentMonthAnalyze) {
        Long beforeMonthTotal = bookLineRepository.totalExpenseForBeforeMonth(request);
        return currentMonthAnalyze.calculateDifferenceWith(beforeMonthTotal);
    }

    private BookAnalyze saveAnalyze(AnalyzeByCategoryRequest request, List<AnalyzeByCategory> analyzeResult) {
        BookAnalyze analyze = BookAnalyze.builder()
            .analyzeDate(request.getLocalDate())
            .book(findBook(request.getBookKey()))
            .category(categoryRepository.findFlowCategory(request.getRoot()))
            .analyzeResult(analyzeResult)
            .build();

        return analyzeRepository.save(analyze);
    }
}
