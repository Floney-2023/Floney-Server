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

    @PostMapping()
    public ResponseEntity<?> initBook(@RequestBody CreateBookRequest request,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.createBook(userDetails, request), HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody CreateBookRequest request,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.addBook(userDetails, request), HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinWithCode(@RequestBody CodeJoinRequest code,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.joinWithCode(userDetails, code), HttpStatus.ACCEPTED);
    }

    @PostMapping("/lines")
    public ResponseEntity<?> createBookLine(@RequestBody CreateLineRequest request,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookLineService.createBookLine(userDetails.getUsername(),request), HttpStatus.CREATED);
    }

    @GetMapping("/month")
    public ResponseEntity<?> showByMonth(@RequestParam("bookKey") String bookKey,
                                         @RequestParam("date") String date) {
        return new ResponseEntity<>(bookLineService.showByMonth(bookKey, date), HttpStatus.OK);
    }

    @GetMapping("/days")
    public ResponseEntity<?> showByDays(@RequestParam("bookKey") String bookKey,
                                        @RequestParam("date") String date) {
        return new ResponseEntity<>(bookLineService.showByDays(bookKey, date), HttpStatus.OK);
    }

    @PostMapping("/name")
    public ResponseEntity<?> changeName(@RequestBody BookNameChangeRequest request) {
        bookService.changeBookName(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBook(@RequestParam("bookKey") String bookKey,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookService.deleteBook(userDetails.getUsername(), bookKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMyBookInfo(@RequestParam("bookKey") String bookKey,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(bookService.getBookInfo(bookKey, userDetails.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/info/bookImg")
    public ResponseEntity<?> updateBookImg(@RequestBody UpdateBookImgRequest request) {
        bookService.updateBookImg(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/info/seeProfile")
    public ResponseEntity<?> changeSeeProfile(@RequestBody SeeProfileRequest request) {
        bookService.updateSeeProfile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/info/asset")
    public ResponseEntity<?> updateAsset(@RequestBody UpdateAssetRequest request) {
        bookService.updateAsset(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/info/budget")
    public ResponseEntity<?> updateBudget(@RequestBody UpdateBudgetRequest request) {
        bookService.updateBudget(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/lines/delete")
    public ResponseEntity<?> deleteAll(String bookKey) {
        bookLineService.deleteAllLine(bookKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users/check")
    public ResponseEntity<?> checkIsBookUser(@AuthenticationPrincipal CustomUserDetails userDetail) {
        return new ResponseEntity<>(bookService.checkIsBookUser(userDetail.getUsername()), HttpStatus.OK);
    }

    @PostMapping("/outcomes")
    public ResponseEntity<?> allOutcomes(@AuthenticationPrincipal CustomUserDetails userDetail,
                                         @RequestBody AllOutcomesRequest allOutcomesRequest) {
        return new ResponseEntity<>(bookLineService.allOutcomes(userDetail.getUsername(),allOutcomesRequest), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> findUsersByBookExceptCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @RequestParam String bookKey) {
        return new ResponseEntity<>(bookService.findUsersByBookExceptCurrentUser(userDetails, bookKey), HttpStatus.OK);
    }
}
