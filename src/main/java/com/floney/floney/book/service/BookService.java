package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;

import java.util.List;

public interface BookService {

    CreateBookResponse createBook(final User user, final CreateBookRequest request);

    CreateBookResponse addBook(final User user, final CreateBookRequest request);

    CreateBookResponse joinWithCode(final CustomUserDetails userDetails, final CodeJoinRequest code);

    void changeBookName(final BookNameChangeRequest request);

    void deleteBook(final String email, final String bookKey);

    OurBookInfo getBookInfo(final String bookKey, final String myEmail);

    void updateBookImg(final UpdateBookImgRequest request);

    void updateSeeProfile(final SeeProfileRequest request);

    void updateCarryOver(final CarryOverRequest request);

    void saveOrUpdateAsset(final UpdateAssetRequest request);

    void saveOrUpdateBudget(final UpdateBudgetRequest request);

    InvolveBookResponse findInvolveBook(final User user);

    void bookUserOut(final BookUserOutRequest request, final String username);


    void deleteBookLine(Book bookUserBook, BookUser bookUser);

    InviteCodeResponse inviteCode(final String bookKey);

    List<BookUserResponse> findUsersByBook(final CustomUserDetails userDetails, final String bookKey);

    CurrencyResponse changeCurrency(final ChangeCurrencyRequest request);

    Book makeInitBook(final String bookKey);

    CurrencyResponse getCurrency(String bookKey);

    BookInfoResponse getBookInfoByCode(String code);
}
