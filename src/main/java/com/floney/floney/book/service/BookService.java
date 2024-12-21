package com.floney.floney.book.service;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.process.OurBookInfo;
import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.dto.response.*;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;

import java.time.Month;
import java.util.List;
import java.util.Map;

public interface BookService {

    CreateBookResponse createBook(final User user, final CreateBookRequest request);

    CreateBookResponse joinWithCode(final String device,final CustomUserDetails userDetails, final CodeJoinRequest code);

    void changeBookName(final BookNameChangeRequest request);

    void deleteBook(final User user, final String bookKey);

    OurBookInfo getBookInfo(final String bookKey, final String myEmail);

    void updateBookImg(final UpdateBookImgRequest request);

    void updateSeeProfile(final SeeProfileRequest request);

    void updateCarryOver(final CarryOverRequest request);

    void saveOrUpdateAsset(final UpdateAssetRequest request);

    void saveOrUpdateBudget(final UpdateBudgetRequest request);

    InvolveBookResponse findInvolveBook(final User user);

    void bookUserOut(final BookUserOutRequest request, final User user);

    InviteCodeResponse inviteCode(final String bookKey);

    List<BookUserResponse> findUsersByBook(final CustomUserDetails userDetails, final String bookKey);

    CurrencyResponse changeCurrency(final ChangeCurrencyRequest request);

    void resetBook(final String bookKey);

    CurrencyResponse getCurrency(String bookKey);

    BookInfoResponse getBookInfoByCode(String code);

    LastSettlementDateResponse getPassedDaysAfterLastSettlementDate(String userEmail, String bookKey);

    Map<Month, Double> getBudgetByYear(String bookKey, String year);

    void leaveBooksBy(long userId);

    void deleteRepeatLine(long repeatLineKey);

    List<RepeatBookLineResponse> getAllRepeatBookLine(String bookKey, CategoryType categoryType);
}
