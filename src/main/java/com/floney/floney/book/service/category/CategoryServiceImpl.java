package com.floney.floney.book.service.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.RepeatBookLineRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.category.SubcategoryRepository;
import com.floney.floney.book.repository.favorite.FavoriteRepository;
import com.floney.floney.book.service.BookLineService;
import com.floney.floney.common.exception.book.AlreadyExistException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final BookRepository bookRepository;
    private final BookLineService bookLineService;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final RepeatBookLineRepository repeatBookLineRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public CreateCategoryResponse createSubcategory(final String bookKey, final CreateCategoryRequest request) {
        final Category category = findCategory(request.getParent());
        final Book book = findBook(bookKey);

        final Subcategory subcategory = Subcategory.of(category, book, request.getName());
        try {
            subcategoryRepository.save(subcategory);
        } catch (final DataIntegrityViolationException e) {
            throw new AlreadyExistException(subcategory.getName());
        }

        return CreateCategoryResponse.of(subcategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo> findAllSubcategoriesByCategory(final String bookKey, final String categoryName) {
        final CategoryType categoryType = CategoryType.findByMeaning(categoryName);
        return categoryRepository.findSubcategoryInfos(categoryType, bookKey);
    }

    @Override
    public void deleteSubcategory(final String bookKey, final DeleteCategoryRequest request) {
        final Category category = findCategory(request.getParent());
        final Book book = findBook(bookKey);

        final Subcategory subcategory = categoryRepository.findSubcategory(category, book, request.getName())
            .orElseThrow(() -> new NotFoundCategoryException((request.getName())));

        categoryRepository.findAllBookLineBySubCategory(subcategory)
            .forEach((bookLine) -> {
                // 예산, 자산, 이월 설정 관련 내역 모두 삭제
                bookLineService.deleteLine(bookLine.getId());
            });

        repeatBookLineRepository.inactiveAllBySubcategory(subcategory);
        favoriteRepository.inactiveAllBySubcategory(subcategory);
        subcategoryRepository.inactive(subcategory);
    }

    @Override
    public void deleteAllBookLineCategory(final long bookLineId) {
        bookLineCategoryRepository.inactiveAllByBookLineId(bookLineId);
    }

    private Category findCategory(final String name) {
        CategoryType categoryType = CategoryType.findByMeaning(name);
        return categoryRepository.findByType(categoryType)
            .orElseThrow(() -> new NotFoundCategoryException(name));
    }

    private Book findBook(final String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }
}
