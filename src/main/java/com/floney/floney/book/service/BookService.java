package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookResponse;
import com.floney.floney.book.dto.CreateBookRequest;

public interface BookService {

    BookResponse createBook(String email, CreateBookRequest request);

    BookResponse joinWithCode(String email, String code);
}
