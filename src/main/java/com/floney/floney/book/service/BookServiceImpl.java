package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookResponse;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;

    @Override
    @Transactional
    public BookResponse createBook(String email, CreateBookRequest request) {
        Book newBook = request.of(email);
        Book savedBook = bookRepository.save(newBook);

        bookUserRepository.save(BookUser.of(findUser(email), savedBook));
        return BookResponse.of(savedBook);
    }

    @Override
    public BookResponse joinWithCode(String email, String code) {
        Book book = bookRepository.findBookByCode(code)
            .orElseThrow(NotFoundBookException::new);
        bookUserRepository.existBookUser(email, code);
        bookUserRepository.isMax(book);
        bookUserRepository.save(BookUser.of(findUser(email), book));

        return BookResponse.of(book);
    }

    @Override
    public void changeBookName(String bookKey, String requestName) {
        Book book = findBook(bookKey);
        book.updateName(requestName);
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(String email, String bookKey) {
        Book book = findBook(bookKey);
        book.isProvider(email);
        bookUserRepository.countBookUser(book);
        BookUser owner = bookUserRepository.findByEmailAndBook(email,book);

        book.delete();
        owner.delete();

        bookRepository.save(book);
        bookUserRepository.save(owner);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private Book findBook(String bookKey){
        return bookRepository.findBookByBookKey(bookKey)
            .orElseThrow(NotFoundBookException::new);
    }


}
