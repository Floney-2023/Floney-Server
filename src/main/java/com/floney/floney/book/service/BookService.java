package com.floney.floney.book.service;

import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.dto.CreateBookResponse;

public interface BookService {

    CreateBookResponse createBook(String email, CreateBookRequest request);

    CreateBookResponse addBook(String email, CreateBookRequest request);

    CreateBookResponse joinWithCode(String email, String code);
    
    void changeBookName(String requestName, String bookKey);

    void deleteBook(String email, String bookKey);
}
