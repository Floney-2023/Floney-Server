package com.floney.floney.book.service;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.request.FavoriteCreateRequest;
import com.floney.floney.book.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    FavoriteResponse createFavorite(String bookKey, String userEmail, FavoriteCreateRequest request);

    FavoriteResponse getFavorite(String bookKey, long id, String userEmail);

    List<FavoriteResponse> getFavoritesByLineCategory(String bookKey, CategoryType categoryType, String userEmail);

    void deleteFavorite(String bookKey, long id, String userEmail);
}
