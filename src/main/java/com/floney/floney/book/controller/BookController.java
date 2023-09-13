package com.floney.floney.book.controller;

import com.floney.floney.book.dto.request.*;
import com.floney.floney.book.service.BookLineService;
import com.floney.floney.book.service.BookService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final BookLineService bookLineService;

    /**
     * 최초 가계부 생성
     *
     * @return CreateBookResponse 생성된 가게부 정보
     * @body CreateBookRequest 가계부 생성 요청용 기본 정보
     */
    @PostMapping()
    public ResponseEntity<?> initBook(@RequestBody CreateBookRequest request,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.createBook(userDetails.getUser(), request), HttpStatus.CREATED);
    }

    /**
     * 가계부 추가
     *
     * @return CreateBookResponse 생성된 가게부 정보
     * @body CreateBootRequest 가계부 생성 요청용 기본 정보
     */
    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody CreateBookRequest request,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.addBook(userDetails.getUser(), request), HttpStatus.CREATED);
    }

    /**
     * 초대코드로 가계부 참여
     *
     * @return CreateBookResponse 참여한 가게부 정보
     * @body CodeJoinRequest 초대 코드
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinWithCode(@RequestBody CodeJoinRequest code,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.joinWithCode(userDetails, code), HttpStatus.ACCEPTED);
    }

    /**
     * 가계부 내역 생성
     *
     * @return CreateBookResponse 추가된 가계부 내역
     * @body CreateLineRequest 가계부 내역
     */
    @PostMapping("/lines")
    public ResponseEntity<?> createBookLine(@RequestBody CreateLineRequest request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookLineService.createBookLine(userDetails.getUsername(), request), HttpStatus.CREATED);
    }

    /**
     * 월별 가계부 내역 조회
     *
     * @param bookKey 가계부 식별 키
     * @param date    조회 요청 월(YYYY-MM-DD)
     * @return MonthLinesResponse 해당 월 전체 가계부 내역
     */
    @GetMapping("/month")
    public ResponseEntity<?> showByMonth(@RequestParam("bookKey") String bookKey,
                                         @RequestParam("date") String date) {
        return new ResponseEntity<>(bookLineService.showByMonth(bookKey, date), HttpStatus.OK);
    }

    /**
     * 일별 가계부 내역 조회
     *
     * @param bookKey 가계부 식별 키
     * @param date    조회 요청 일자(YYYY-MM-DD)
     * @return TotalDayLinesResponse 해당 일자 전체 가계부 내역
     */
    @GetMapping("/days")
    public ResponseEntity<?> showByDays(@RequestParam("bookKey") String bookKey,
                                        @RequestParam("date") String date) {
        return new ResponseEntity<>(bookLineService.showByDays(bookKey, date), HttpStatus.OK);
    }

    /**
     * 가계부 이름 변경
     *
     * @body BookNameChangeRequest 변경하고자 하는 가계부 이름
     */
    @PostMapping("/name")
    public ResponseEntity<?> changeName(@RequestBody BookNameChangeRequest request) {
        bookService.changeBookName(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 가계부 삭제하기(팀장만 호출 가능)
     *
     * @param bookKey 가계부 식별자
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBook(@RequestParam("bookKey") String bookKey,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookService.deleteBook(userDetails.getUsername(), bookKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 가계부 설정
     *
     * @param bookKey 가계부 식별자
     * @return OurBookInfo 가계부 설정 정보
     */
    @GetMapping("/info")
    public ResponseEntity<?> getMyBookInfo(@RequestParam("bookKey") String bookKey,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.getBookInfo(bookKey, userDetails.getUsername()), HttpStatus.OK);
    }

    /**
     * 가계부 이미지 변경
     *
     * @body UpdateBookImgRequest 변경하고자 하는 가계부 이미지
     */
    @PostMapping("/info/bookImg")
    public ResponseEntity<?> updateBookImg(@RequestBody UpdateBookImgRequest request) {
        bookService.updateBookImg(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 내역 조회 시 유저 이미지 공개 여부 변경
     *
     * @body SeeProfileRequest 공개 여부
     */
    @PostMapping("/info/seeProfile")
    public ResponseEntity<?> changeSeeProfile(@RequestBody SeeProfileRequest request) {
        bookService.updateSeeProfile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 이월 내역 설정 변경
     *
     * @body CarryOverRequest 이월 내역 기능 ON/OFF 여부
     */
    @PostMapping("/info/carryOver")
    public ResponseEntity<?> changeCarryOver(@RequestBody CarryOverRequest request) {
        bookService.updateCarryOver(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 자산 변경
     *
     * @body UpdateAssetRequest 자산 변경 정보
     */
    @PostMapping("/info/asset")
    public ResponseEntity<?> updateAsset(@RequestBody UpdateAssetRequest request) {
        bookService.saveOrUpdateAsset(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 예산 변경
     *
     * @body UpdateBudgetRequest 예산 변경 정보
     */
    @PostMapping("/info/budget")
    public ResponseEntity<?> updateBudget(@RequestBody UpdateBudgetRequest request) {
        bookService.saveOrUpdateBudget(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 가계부 초기화
     *
     * @param bookKey 가계부 식별자
     */
    @DeleteMapping("/info/delete/all")
    public ResponseEntity<?> deleteAll(String bookKey) {
        bookService.makeInitBook(bookKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 유저 가계부 유효 확인
     *
     * @return CheckBookValidResponse 참여하는 가계부 식별키
     * 유저가 참여하는 가계부가 있다면
     * 가장 최근에 접근한 가계부의 식별 키를 전송
     */
    @GetMapping("/users/check")
    public ResponseEntity<?> findInvolveBook(@AuthenticationPrincipal CustomUserDetails userDetail) {
        return new ResponseEntity<>(bookService.findInvolveBook(userDetail.getUser()), HttpStatus.OK);
    }

    /**
     * 기간 내의 모든 지출 내역 조회
     *
     * @return List<DayLines> 지출 내역
     * @body AllOutcomesRequest 기간, 가계부 식별키 정보
     */
    @PostMapping("/outcomes")
    public ResponseEntity<?> allOutcomes(@RequestBody AllOutcomesRequest allOutcomesRequest) {
        return new ResponseEntity<>(bookLineService.allOutcomes(allOutcomesRequest), HttpStatus.OK);
    }

    /**
     * 가계부의 모든 유저들 조회
     *
     * @param userDetails 현재 접속한 유저 정보
     * @param bookKey     가계부 키
     * @return 가계부 유저들 리스트
     */
    @GetMapping("/users")
    public ResponseEntity<?> findUsersByBook(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @RequestParam String bookKey) {
        return new ResponseEntity<>(bookService.findUsersByBook(userDetails, bookKey), HttpStatus.OK);
    }

    /**
     * 가계부 나가기(팀원만 호출 가능)
     *
     * @body BookUserOutRequest 가계부 식별키 정보
     */
    @PostMapping("/users/out")
    public ResponseEntity<?> bookUserOut(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody BookUserOutRequest request) {
        bookService.bookUserOut(request, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 가계부 초대코드 조회
     *
     * @param bookKey 가계부 식별키
     * @return InviteCodeResponse 가계부 초대코드
     */
    @GetMapping("/code")
    public ResponseEntity<?> getInviteCode(@RequestParam String bookKey) {
        return new ResponseEntity<>(bookService.inviteCode(bookKey), HttpStatus.OK);
    }

    /**
     * 화폐 통화 변경
     * @body ChangeCurrencyRequest 변경할 통화 정보
     * @return 변경한 통화 정보
     */
    @PostMapping("/info/currency")
    public ResponseEntity<?> changeCurrency(@RequestBody ChangeCurrencyRequest request) {
        return new ResponseEntity<>(bookService.changeCurrency(request), HttpStatus.OK);
    }
  
    /**
     * 가계부 내역 수정
     *
     * @return InviteCodeResponse 가계부 내역
     * @body ChangeBookLineRequest 수정한 가계부 내역
     */
    @PostMapping("/lines/change")
    public ResponseEntity<?> changeBookLine(@RequestBody ChangeBookLineRequest request) {
        return new ResponseEntity<>(bookLineService.changeLine(request), HttpStatus.OK);
    }

    /**
     * 가계부 내역 삭제
     *
     * @param bookLineKey 가계부 내역 PK
     */
    @DeleteMapping("/lines/delete")
    public ResponseEntity<?> deleteBookLine(@RequestParam Long bookLineKey) {
        bookLineService.deleteLine(bookLineKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 가계부 화폐정보 조회
     * @param bookKey 가계부 키
     * @return CurrencyResponse 화폐정보
     */
    @GetMapping("/info/currency")
    public ResponseEntity<?> getCurrency(@RequestParam String bookKey) {
        return new ResponseEntity<>(bookService.getCurrency(bookKey), HttpStatus.OK);
    }

    /**
     * 참여코드로 가계부 정보 조회
     * @param code 가계부 참여코드
     * @return BookResponse 가계부 정보
     */
    @GetMapping()
    public ResponseEntity<?> getBookInfo(@RequestParam String code) {
        return new ResponseEntity<>(bookService.getBookInfoByCode(code), HttpStatus.OK);
    }

    /**
     * 가계부의 마지막 정산 날짜로부터 며칠이 지났는 지 조회
     *
     * @param bookKey 가계부 key
     * @return LastSettlementDateResponse 마지막 정산 날짜로부터 지난 날짜
     */
    @GetMapping("/settlement/last")
    public ResponseEntity<?> getLastSettlement(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam String bookKey) {
        return ResponseEntity.ok(bookService.getPassedDaysAfterLastSettlementDate(userDetails.getUsername(), bookKey));
    }
    /**
     * 가계부 비활성화 / 활성화 여부 조회
     * @param bookKey 가계부 키
     * @return BookStatusResponse 비활성화 / 활성화 여부
     */
    @GetMapping("/bookStatus")
    public ResponseEntity<?> getBookStatus(@RequestParam String bookKey) {
        return new ResponseEntity<>(bookService.getBookStatus(bookKey), HttpStatus.OK);
    }

}
