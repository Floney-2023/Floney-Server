package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookNameChangeRequest;
import com.floney.floney.book.dto.CodeJoinRequest;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.dto.CreateBookResponse;

public interface BookService {

    CreateBookResponse createBook(String email, CreateBookRequest request);

    CreateBookResponse addBook(String email, CreateBookRequest request);

    CreateBookResponse joinWithCode(String email, CodeJoinRequest code);
    
    void changeBookName(BookNameChangeRequest request);

    void deleteBook(String email, String bookKey);
}
