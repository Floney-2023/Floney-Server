package com.floney.floney.book.controller;

import com.floney.floney.book.dto.CreateBookRequest;
import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.service.BookLineService;
import com.floney.floney.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    private final BookLineService bookLineService;

    @PostMapping()
    public ResponseEntity<?> initBook(@RequestBody CreateBookRequest request) {
        return new ResponseEntity<>(bookService.createBook(userAuth(), request), HttpStatus.CREATED);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody CreateBookRequest request) {
        return new ResponseEntity<>(bookService.addBook(userAuth(), request), HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinWithCode(@RequestParam("code") String code) {
        return new ResponseEntity<>(bookService.joinWithCode(userAuth(), code), HttpStatus.ACCEPTED);
    }

    @PostMapping("/lines")
    public ResponseEntity<?> createBookLine(@RequestBody CreateLineRequest request) {
        return new ResponseEntity<>(bookLineService.createBookLine(request), HttpStatus.CREATED);
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

    private String userAuth() {
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();

        return authentication.getName();
    }

}
