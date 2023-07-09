package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;
import com.floney.floney.book.entity.Book;
import com.floney.floney.user.dto.security.CustomUserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface BookService {

    CreateBookResponse createBook(CustomUserDetails userDetails, CreateBookRequest request);

    CreateBookResponse addBook(CustomUserDetails userDetails, CreateBookRequest request);

    CreateBookResponse joinWithCode(CustomUserDetails userDetails, CodeJoinRequest code);

    void changeBookName(BookNameChangeRequest request);

    void deleteBook(String email, String bookKey);

    OurBookInfo getBookInfo(String bookKey, String myEmail);

    void updateBookImg(UpdateBookImgRequest request);

    void updateSeeProfile(SeeProfileRequest request);

    void updateAsset(UpdateAssetRequest request);

    void updateBudget(UpdateBudgetRequest request);

    CheckBookValidResponse checkIsBookUser(String email);

    Book findBook(Long bookId);
}
