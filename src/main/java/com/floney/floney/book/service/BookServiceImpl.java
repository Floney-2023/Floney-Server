package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.LimitRequestException;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotSubscribeException;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private static final int SUBSCRIBE_MAX = 2;
    private static final int DEFAULT_MAX = 1;

    @Override
    @Transactional
    public CreateBookResponse createBook(String email, CreateBookRequest request) {
        Book newBook = request.of(email);
        Book savedBook = bookRepository.save(newBook);

        bookUserRepository.save(BookUser.of(findUser(email), savedBook));
        return CreateBookResponse.of(savedBook);
    }

    @Transactional
    @Override
    public CreateBookResponse addBook(String email, CreateBookRequest request) {
        User requestUser = findUser(email);
        int count = bookUserRepository.countBookUserByUserAndStatus(requestUser, Status.ACTIVE);
        if (requestUser.isSubscribe()) {
            return subscribeCreateBook(count, email, request);
        } else {
            return notSubscribeCreateBook(count, email, request);
        }
    }

    @Transactional
    public CreateBookResponse subscribeCreateBook(int count, String email, CreateBookRequest request) {
        if (count >= SUBSCRIBE_MAX) {
            throw new LimitRequestException();
        }
        return createBook(email, request);
    }

    @Transactional
    public CreateBookResponse notSubscribeCreateBook(int count, String email, CreateBookRequest request) {
        if (count >= DEFAULT_MAX) {
            throw new NotSubscribeException();
        }
        return createBook(email, request);
    }

    @Override
    public CreateBookResponse joinWithCode(String email, CodeJoinRequest request) {
        String code = request.getCode();
        Book book = bookRepository.findBookByCodeAndStatus(code, Status.ACTIVE)
            .orElseThrow(NotFoundBookException::new);
        bookUserRepository.isMax(book);
        bookUserRepository.save(BookUser.of(findUser(email), book));

        return CreateBookResponse.of(book);
    }

    @Override
    public void changeBookName(BookNameChangeRequest request) {
        Book book = findBook(request.getBookKey());
        book.updateName(request.getName());
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(String email, String bookKey) {
        Book book = findBook(bookKey);
        book.isOwner(email);
        bookUserRepository.countBookUser(book);
        BookUser owner = bookUserRepository.findByEmailAndBook(email, book);

        book.delete();
        owner.delete();

        bookRepository.save(book);
        bookUserRepository.save(owner);
    }

    @Override
    public OurBookInfo getBookInfo(String bookKey, String myEmail) {
        List<OurBookUser> bookUsers = bookUserRepository.findAllUser(bookKey);
        return OurBookInfo.of(findBook(bookKey), bookUsers, myEmail);
    }

    private User findUser(String email) {
        return userRepository.findUserByEmailAndStatus(email,Status.ACTIVE)
            .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(NotFoundBookException::new);
    }
}
