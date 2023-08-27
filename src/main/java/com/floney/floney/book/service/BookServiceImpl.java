package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
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

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineCustomRepository bookLineRepository;
    private final UserRepository userRepository;
    private final CategoryCustomRepository categoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    @Override
    @Transactional
    public CreateBookResponse createBook(CustomUserDetails userDetails, CreateBookRequest request) {
        User requestUser = userDetails.getUser();
        Book newBook = request.of(userDetails.getUsername());

        Book savedBook = bookRepository.save(newBook);
        saveDefaultBookKey(requestUser, savedBook);

        bookUserRepository.save(BookUser.of(requestUser, savedBook));
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
    public CreateBookResponse subscribeCreateBook(int count,
                                                  CustomUserDetails userDetails,
                                                  CreateBookRequest request) {
        if (count >= SUBSCRIBE_MAX) {
            throw new LimitRequestException();
        }
        return createBook(userDetails, request);
    }

    @Transactional
    public CreateBookResponse notSubscribeCreateBook(int count,
                                                     CustomUserDetails userDetails,
                                                     CreateBookRequest request) {
        if (count >= DEFAULT_MAX) {
            throw new NotSubscribeException();
        }
        return createBook(userDetails, request);
    }

    private void saveDefaultBookKey(User user, Book book) {
        user.saveDefaultBookKey(book.getBookKey());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public CreateBookResponse joinWithCode(CustomUserDetails userDetails, CodeJoinRequest request) {
        String code = request.getCode();

        Book book = bookRepository.findBookByCodeAndStatus(code, Status.ACTIVE)
            .orElseThrow(NotFoundBookException::new);

        bookUserRepository.isMax(book);
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
            .orElseThrow(UserNotFoundException::new);
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
        return CurrencyResponse.of(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void bookUserOut(BookUserOutRequest request, String userEmail) {
        BookUser bookUser = findBookUserByKey(userEmail, request.getBookKey());
        deleteBookLineBy(bookUser, request.getBookKey());
        deleteBookUser(bookUser);
    }

    @Override
    @Transactional(readOnly = true)
    public InviteCodeResponse inviteCode(String bookKey) {
        return new InviteCodeResponse(findBook(bookKey));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(NotFoundBookException::new);
    }

    private void isValidToDeleteBook(Book book, String email) {
        book.isOwner(email);
        bookUserRepository.countBookUser(book);
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
            .orElseThrow(NotFoundBookUserException::new);
    }

    private void deleteBookLineBy(BookUser bookUser, String bookKey) {
        bookLineRepository.deleteAllLinesByUser(bookUser, bookKey);
    }

    private void makeInitBook(String bookKey){
        Book book = findBook(bookKey);
        bookLineRepository.deleteAllLines(bookKey);
        book.initBook();
        categoryRepository.deleteAllCustomCategory(book);
        bookLineCategoryRepository.deleteBookLineCategory(bookKey);
        bookRepository.save(book);
    }
}
