package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.entity.Book;
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

    void updateCarryOver(final CarryOverRequest request);

    void updateAsset(final UpdateAssetRequest request);

    void updateBudget(final UpdateBudgetRequest request);

    InvolveBookResponse findInvolveBook(final String email);

    void bookUserOut(final BookUserOutRequest request, final String username);

    InviteCodeResponse inviteCode(final String bookKey);

    List<BookUserResponse> findUsersByBook(final CustomUserDetails userDetails, final String bookKey);

    CurrencyResponse changeCurrency(final ChangeCurrencyRequest request);

    Book makeInitBook(final String bookKey);
}
