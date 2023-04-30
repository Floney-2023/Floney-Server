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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        return BookResponse.of(bookRepository.findBookByProvider(email));
    }

    @Override
    public BookResponse joinWithCode(String email, String code) {
        Book book = bookRepository.findBookByCode(code)
            .orElseThrow(NotFoundBookException::new);

        bookUserRepository.isMax(book);
        bookUserRepository.save(BookUser.of(findUser(email), book));

        return BookResponse.of(book);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email);
    }
}
