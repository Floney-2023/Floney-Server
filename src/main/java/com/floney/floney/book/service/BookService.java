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
import com.floney.floney.book.dto.response.BookUserResponse;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.dto.response.InviteCodeResponse;
import com.floney.floney.book.dto.response.InvolveBookResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import java.util.List;

public interface BookService {

    CreateBookResponse createBook(final CustomUserDetails userDetails, final CreateBookRequest request);

    CreateBookResponse addBook(final CustomUserDetails userDetails, final CreateBookRequest request);

    CreateBookResponse joinWithCode(final CustomUserDetails userDetails, final CodeJoinRequest code);

    void changeBookName(final BookNameChangeRequest request);

    void deleteBook(final String email, final String bookKey);

    OurBookInfo getBookInfo(final String bookKey, final String myEmail);

    void updateBookImg(final UpdateBookImgRequest request);

    void updateSeeProfile(final SeeProfileRequest request);

    void updateAsset(final UpdateAssetRequest request);

    void updateBudget(final UpdateBudgetRequest request);

    InvolveBookResponse findInvolveBook(final String email);

    void bookUserOut(final BookUserOutRequest request, final String username);

    InviteCodeResponse inviteCode(final String bookKey);

    List<BookUserResponse> findUsersByBook(final CustomUserDetails userDetails, final String bookKey);

    AnalyzeResponse analyzeByCategory(final AnalyzeByCategoryRequest request);
}
