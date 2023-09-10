package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.entity.category.BookCategory;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.*;
import com.floney.floney.common.exception.common.NotSubscribeException;
import com.floney.floney.common.exception.user.UserNotFoundException;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
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

        Book book = bookRepository.findBookByCodeAndStatus(code, Status.ACTIVE)
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
    public void updateCarryOver(CarryOverRequest request) {
        Book savedBook = findBook(request.getBookKey());
        savedBook.changeCarryOverStatus(request.isStatus());
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
    @Transactional(readOnly = true)
    public InvolveBookResponse findInvolveBook(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        return InvolveBookResponse.of(user.getRecentBookKey());
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
    public void deleteBookLine(Book bookUserBook, BookUser bookUser){
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

        bookLineRepository.deleteAllLines(bookKey);
        return bookRepository.save(book);
    }

    @Override
    public CurrencyResponse getCurrency(String bookKey) {
        return CurrencyResponse.of(findBook(bookKey));
    }

    @Override
    public BookInfoResponse getBookInfoByCode(String code) {
        Book book = bookRepository.findBookByCodeAndStatus(code, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(code));
        long memberCount = bookUserRepository.countBookUser(book);
        return BookInfoResponse.of(book, memberCount);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
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
        return bookUserRepository.findAllByBookAndStatus(findBook(bookKey), Status.ACTIVE);
    }

    private void deleteBookUser(BookUser bookUser) {
        bookUser.delete();
        bookUserRepository.save(bookUser);
    }

    private BookUser findBookUserByKey(String userEmail, String bookKey) {
        return bookUserRepository.findBookUserByKey(userEmail, bookKey)
            .orElseThrow(() -> new NotFoundBookUserException(bookKey, userEmail));
    }

    private void deleteBookLineBy(BookUser bookUser, String bookKey) {
        bookLineRepository.deleteAllLinesByUser(bookUser, bookKey);
    }

    private void saveDefaultBookKey(User user, Book book) {
        user.saveDefaultBookKey(book.getBookKey());
        userRepository.save(user);
    }

     private void checkCreateBookMaximum(User user) {
        int currentParticipateCount = bookUserRepository.countBookUserByUserAndStatus(user, Status.ACTIVE);

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


}
