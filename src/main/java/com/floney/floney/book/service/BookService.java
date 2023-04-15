package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookResponse;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Book newBook = request.of(CodeFactory.generateCode());
        Book savedBook = bookRepository.save(newBook);
        return BookResponse.of(savedBook);
    }

}
