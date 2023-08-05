package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.AnalyzeResponse;
import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.BookUserResponse;
import com.floney.floney.book.dto.response.CheckBookValidResponse;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.InviteCodeResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;

import java.time.LocalDate;
import java.util.List;

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

    List<BookUserResponse> findUsersByBook(CustomUserDetails userDetails, String bookKey);

    void bookUserOut(BookUserOutRequest request, String username);

    InviteCodeResponse inviteCode(String bookKey);

    AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request);
}
