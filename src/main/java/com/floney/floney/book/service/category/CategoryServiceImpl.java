package com.floney.floney.book.service.category;

import com.floney.floney.book.domain.category.Category;
import com.floney.floney.book.domain.category.CustomSubCategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.category.CustomSubCategoryRepository;
import com.floney.floney.book.service.BookLineService;
import com.floney.floney.common.exception.book.AlreadyExistException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CustomSubCategoryRepository customSubCategoryRepository;
    private final BookRepository bookRepository;
    private final BookLineService bookLineService;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    @Override
    public CreateCategoryResponse createUserCategory(final CreateCategoryRequest request) {
        final Category category = findCategory(request.getParent());
        final Book book = findBook(request.getBookKey());

        validateDifferentSubCategory(book, category, request.getName());

        final CustomSubCategory subCategory = CustomSubCategory.of(category, book, request.getName());
        customSubCategoryRepository.save(subCategory);

        return CreateCategoryResponse.of(subCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo> findAllBy(final String categoryName, final String bookKey) {
        return categoryRepository.findAllCategory(categoryName, bookKey);
    }

    @Override
    public void deleteCustomCategory(final DeleteCategoryRequest request) {
        final Category category = findCategory(request.getRoot());
        final Book book = findBook(request.getBookKey());

        final CustomSubCategory subCategory = categoryRepository.findCustomTarget(category, book, request.getName())
            .orElseThrow(() -> new NotFoundCategoryException((request.getName())));

        categoryRepository.findAllBookLineByCategory(subCategory)
            .forEach((bookLine) -> {
                // 예산, 자산, 이월 설정 관련 내역 모두 삭제
                bookLineService.deleteLine(bookLine.getId());
            });

        subCategory.inactive();
    }

    @Override
    public void deleteAllBookLineCategory(final long bookLineId) {
        bookLineCategoryRepository.inactiveAllByBookLineId(bookLineId);
    }

    private Category findCategory(final String request) {
        return categoryRepository.findParentCategory(request)
            .orElseThrow(() -> new NotFoundCategoryException(request));
    }

    private void validateDifferentSubCategory(final Book book,
                                              final Category parent,
                                              final String name) {
        categoryRepository.findCustomTarget(parent, book, name)
            .ifPresent(subCategory -> {
                throw new AlreadyExistException(subCategory.getName());
            });
    }

    private Book findBook(final String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }
}
