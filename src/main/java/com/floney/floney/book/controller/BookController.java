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
    public ResponseEntity createBook(@RequestBody CreateBookRequest request) {
        return new ResponseEntity<>(bookService.createBook(userAuth(), request), HttpStatus.CREATED);
    }

    @PostMapping("/{code}")
    public ResponseEntity joinWithCode(@PathVariable(value = "code") String code) {
        return new ResponseEntity<>(bookService.joinWithCode(userAuth(), code), HttpStatus.ACCEPTED);
    }

    @PostMapping("/book_line")
    public ResponseEntity createBookLine(@RequestBody CreateLineRequest request){
        return new ResponseEntity<>(bookLineService.addBookLine(request),HttpStatus.CREATED);

    }

    private String userAuth() {
        Authentication authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();

        return authentication.getName();
    }

}
