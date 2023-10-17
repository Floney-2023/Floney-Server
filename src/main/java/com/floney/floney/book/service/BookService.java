package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.dto.response.AlarmResponse;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;

import java.time.Month;
import java.util.List;
import java.util.Map;

public interface BookService {
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

    void bookUserOut(final BookUserOutRequest request, final User user);

    void deleteBookLine(Book bookUserBook, BookUser bookUser);

    InviteCodeResponse inviteCode(final String bookKey);

    List<BookUserResponse> findUsersByBook(final CustomUserDetails userDetails, final String bookKey);

    CurrencyResponse changeCurrency(final ChangeCurrencyRequest request);

    Book makeInitBook(final String bookKey);

    CurrencyResponse getCurrency(String bookKey);

    BookInfoResponse getBookInfoByCode(String code);

    LastSettlementDateResponse getPassedDaysAfterLastSettlementDate(String userEmail, String bookKey);

    BookStatusResponse getBookStatus(String bookKey);

    Map<Month, Long> getBudgetByYear(String bookKey, String year);

    void saveAlarm(SaveAlarmRequest request);

    void updateAlarmReceived(UpdateAlarmReceived request);

    List<AlarmResponse> getAlarmByBook(String bookKey,String email);
}
