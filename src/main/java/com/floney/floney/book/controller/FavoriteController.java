package com.floney.floney.book.controller;

import com.floney.floney.book.dto.request.FavoriteCreateRequest;
import com.floney.floney.book.dto.response.FavoriteResponse;
import com.floney.floney.book.service.FavoriteService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/books/{key}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse createFavorite(@PathVariable("key") final String bookKey,
                                           @AuthenticationPrincipal final CustomUserDetails userDetails,
                                           @RequestBody final FavoriteCreateRequest request) {
        return favoriteService.createFavorite(bookKey, userDetails.getUsername(), request);
    }
}
