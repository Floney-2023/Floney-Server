package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.AnalyzeResponse;
import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.AnalyzeByCategoryRequest;
import com.floney.floney.book.dto.request.BookNameChangeRequest;
import com.floney.floney.book.dto.request.BookUserOutRequest;
import com.floney.floney.book.dto.request.CodeJoinRequest;
import com.floney.floney.book.dto.request.CreateBookRequest;
import com.floney.floney.book.dto.request.SeeProfileRequest;
import com.floney.floney.book.dto.request.UpdateAssetRequest;
import com.floney.floney.book.dto.request.UpdateBookImgRequest;
import com.floney.floney.book.dto.request.UpdateBudgetRequest;
import com.floney.floney.book.dto.response.CheckBookValidResponse;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.InviteCodeResponse;
import com.floney.floney.user.dto.response.UserResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
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

    List<UserResponse> findUsersByBookExceptCurrentUser(CustomUserDetails userDetails, String bookKey);

    void bookUserOut(BookUserOutRequest request, String username);

    InviteCodeResponse inviteCode(String bookKey);

    AnalyzeResponse analyzeByCategory(AnalyzeByCategoryRequest request);
}
