package com.floney.floney.favorite.controller;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.favorite.dto.MyFavoriteResponseByFlow;
import com.floney.floney.favorite.service.FavoriteService;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

  private final FavoriteService favoriteService;

  @PostMapping("/users/favorites/{bookLineId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> registerMyFavorite(
      @AuthenticationPrincipal final CustomUserDetails userDetails,
      @PathVariable final Long bookLineId
      ) {
      favoriteService.register(userDetails, bookLineId);
      return ResponseEntity.status(HttpStatus.CREATED).build();
  }

    @GetMapping("/users/favorites/{flow}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MyFavoriteResponseByFlow>> showMyFavoritesByCategory(
        @AuthenticationPrincipal final CustomUserDetails userDetails,
        @PathVariable final CategoryType flow
    ) {
        List<MyFavoriteResponseByFlow> responses = favoriteService.showMyFavoritesByCategory(userDetails, flow);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @DeleteMapping("/users/favorites/{bookLineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> cancelMyFavorite(
        @AuthenticationPrincipal final CustomUserDetails userDetails,
        @PathVariable final Long bookLineId
    ) {
        favoriteService.cancel(userDetails, bookLineId);
        return ResponseEntity.noContent().build();
    }

}
