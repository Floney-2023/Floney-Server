package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;
import org.springframework.transaction.annotation.Transactional;

public interface BookService {

    CreateBookResponse createBook(String email, CreateBookRequest request);

    CreateBookResponse addBook(String email, CreateBookRequest request);

    CreateBookResponse joinWithCode(String email, CodeJoinRequest code);

    void changeBookName(BookNameChangeRequest request);

    void deleteBook(String email, String bookKey);

    OurBookInfo getBookInfo(String bookKey, String myEmail);

    void updateBookImg(UpdateBookImgRequest request);

    @Transactional
    void updateSeeProfile(SeeProfileRequest request);
}
