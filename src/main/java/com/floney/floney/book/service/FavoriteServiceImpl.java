package com.floney.floney.book.service;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.favorite.Favorite;
import com.floney.floney.book.dto.request.FavoriteCreateRequest;
import com.floney.floney.book.dto.response.FavoriteResponse;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.favorite.FavoriteRepository;
import com.floney.floney.common.exception.book.FavoriteSizeInvalidException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.floney.floney.book.domain.category.CategoryType.ASSET;
import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public FavoriteResponse createFavorite(final String bookKey,
                                           final String userEmail,
                                           final FavoriteCreateRequest request) {
        validateBookUser(bookKey, userEmail);

        final Book book = findBook(bookKey);
        validateFavoriteSizeByBook(book);

        final Category lineCategory = findLineCategory(request.lineCategoryName());
        final Subcategory lineSubcategory = findLineSubcategory(request.lineSubcategoryName(), lineCategory, book);
        final Subcategory assetSubcategory = findAssetSubcategory(book, request.assetSubcategoryName());

        final Favorite favorite = Favorite.builder()
            .book(book)
            .money(request.money())
            .description(request.description())
            .lineCategory(lineCategory)
            .lineSubcategory(lineSubcategory)
            .assetSubcategory(assetSubcategory)
            .build();
        favoriteRepository.save(favorite);

        return FavoriteResponse.from(favorite);
    }

    private void validateBookUser(final String bookKey, final String userEmail) {
        if (!bookUserRepository.existsByBookKeyAndUserEmail(bookKey, userEmail)) {
            throw new NotFoundBookUserException(bookKey, userEmail);
        }
    }

    private void validateFavoriteSizeByBook(final Book book) {
        final int favoriteSize = favoriteRepository.findAllExclusivelyByBookAndStatus(book, ACTIVE).size();
        if (favoriteSize == Book.FAVORITE_MAX_SIZE) {
            throw new FavoriteSizeInvalidException(book.getBookKey());
        } else if (favoriteSize > Book.FAVORITE_MAX_SIZE) {
            log.error("가계부의 즐겨찾기 개수가 이미 {}개를 초과 - 가계부: {}", Book.FAVORITE_MAX_SIZE, book.getBookKey());
            throw new FavoriteSizeInvalidException(book.getBookKey());
        }
    }

    private Book findBook(final String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private Category findLineCategory(final String categoryName) {
        final CategoryType categoryType = CategoryType.findLineByMeaning(categoryName);
        return categoryRepository.findByType(categoryType)
            .orElseThrow(() -> new NotFoundCategoryException(categoryName));
    }

    private Subcategory findLineSubcategory(final String lineSubCategoryName, final Category lineCategory, final Book book) {
        return categoryRepository.findSubcategory(lineSubCategoryName, book, lineCategory.getName())
            .orElseThrow(() -> new NotFoundCategoryException(lineSubCategoryName));
    }

    private Subcategory findAssetSubcategory(final Book book, final String assetSubCategoryName) {
        return categoryRepository.findSubcategory(assetSubCategoryName, book, ASSET)
            .orElseThrow(() -> new NotFoundCategoryException(assetSubCategoryName));
    }
}
