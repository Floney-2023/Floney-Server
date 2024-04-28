package com.floney.floney.book.controller;

import com.floney.floney.book.dto.request.FavoriteCreateRequest;
import com.floney.floney.book.dto.response.FavoriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    @PostMapping("/books/{key}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse createFavorite(@PathVariable("key") final String bookKey,
                                           @RequestBody final FavoriteCreateRequest request) {
        return null;
    }
}
