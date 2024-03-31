package com.floney.floney.user.controller;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.user.dto.response.MyFavoriteResponseByFlow;
import com.floney.floney.user.service.FavoriteService;
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

    /**
     * 즐겨찾기 둥록
     *
     * @param bookLineId 가계부 내역 PK
     */
  @PostMapping("/users/favorites/{bookLineId}")
  public ResponseEntity<Void> registerMyFavorite(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                 @PathVariable final Long bookLineId) {
      favoriteService.register(userDetails.getUsername(), bookLineId);
      return ResponseEntity.status(HttpStatus.CREATED).build();
  }

    /**
     * 카테고리별 즐겨찾기 내역 조회
     *
     * @param flow 카테고리 (수입/지출/이체)
     * @return List<MyFavoriteResponseByFlow> 즐겨찾기한 가계부 내역 정보
     */
    @GetMapping("/users/favorites/{flow}")
    public ResponseEntity<?> showMyFavoritesByFlow(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                   @PathVariable final CategoryType flow) {
        List<MyFavoriteResponseByFlow> responses = favoriteService.showMyFavoritesByFlow(userDetails.getUsername(), flow);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    /**
     * 즐겨찾기 취소
     *
     * @param bookLineId 가계부 내역 PK
     */
    @DeleteMapping("/users/favorites/{bookLineId}")
    public ResponseEntity<Void> cancelMyFavorite(@AuthenticationPrincipal final CustomUserDetails userDetails,
                                                 @PathVariable final Long bookLineId) {
        favoriteService.cancel(userDetails.getUsername(), bookLineId);
        return ResponseEntity.noContent().build();
    }

}
