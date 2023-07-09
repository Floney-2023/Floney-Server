package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookNameChangeRequest;
import com.floney.floney.book.dto.CheckBookValidResponse;
import com.floney.floney.book.dto.CodeJoinRequest;
import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.dto.CreateBookResponse;
import com.floney.floney.book.dto.OurBookInfo;
import com.floney.floney.book.dto.SeeProfileRequest;
import com.floney.floney.book.dto.UpdateAssetRequest;
import com.floney.floney.book.dto.UpdateBookImgRequest;
import com.floney.floney.book.dto.UpdateBudgetRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import java.time.LocalDate;
import java.util.List;
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

    Book findBook(String bookKey);

    void updateLastSettlementDate(String bookKey, LocalDate settlementDate);

    @Transactional(readOnly = true)
    List<UserResponse> findUsersByBookExceptCurrentUser(CustomUserDetails userDetails, String bookKey);
}
