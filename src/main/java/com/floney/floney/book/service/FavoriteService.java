package com.floney.floney.book.service;

import com.floney.floney.book.dto.request.FavoriteCreateRequest;
import com.floney.floney.book.dto.response.FavoriteResponse;

public interface FavoriteService {

    FavoriteResponse createFavorite(String bookKey, String userEmail, FavoriteCreateRequest request);

    FavoriteResponse getFavorite(String bookKey, long id, String userEmail);
}
