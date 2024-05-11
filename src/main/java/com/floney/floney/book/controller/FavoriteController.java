package com.floney.floney.book.controller;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.request.FavoriteCreateRequest;
import com.floney.floney.book.dto.response.FavoriteResponse;
import com.floney.floney.book.service.FavoriteService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/books/{key}/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse createFavorite(@PathVariable("key") final String bookKey,
                                           @AuthenticationPrincipal final CustomUserDetails userDetails,
                                           @RequestBody @Valid final FavoriteCreateRequest request) {
        return favoriteService.createFavorite(bookKey, userDetails.getUsername(), request);
    }

    @GetMapping("/books/{key}/favorites/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FavoriteResponse getFavorite(@PathVariable("key") final String bookKey,
                                        @PathVariable("id") final long id,
                                        @AuthenticationPrincipal final CustomUserDetails userDetails) {
        return favoriteService.getFavorite(bookKey, id, userDetails.getUsername());
    }

    @GetMapping("/books/{key}/favorites")
    @ResponseStatus(HttpStatus.OK)
    public List<FavoriteResponse> getFavoritesByLineCategory(@PathVariable("key") final String bookKey,
                                                             @RequestParam("categoryType") final CategoryType categoryType,
                                                             @AuthenticationPrincipal final CustomUserDetails userDetails) {
        return favoriteService.getFavoritesByLineCategory(bookKey, categoryType, userDetails.getUsername());
    }

    @DeleteMapping("/books/{key}/favorites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavorite(@PathVariable("key") final String bookKey,
                               @PathVariable("id") final long id,
                               @AuthenticationPrincipal final CustomUserDetails userDetails) {
        favoriteService.deleteFavorite(bookKey, id, userDetails.getUsername());
    }
}
