package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.floney.floney.book.entity.BookLineCategory.of;

@Component
@Getter
@RequiredArgsConstructor
public class CategoryFactory {
    private final CategoryRepository categoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    public BookLineCategory saveLineBookLineCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = findLineCategory(request);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    public BookLineCategory saveFlowBookLineCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = findFlowCategory(request);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    public BookLineCategory saveAssetBookLineCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = findAssetCategory(request);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private Category findLineCategory(CreateLineRequest request) {
        return categoryRepository.findLineCategory(request.getLine(), request.getBookKey(), request.getFlow())
            .orElseThrow(NotFoundCategoryException::new);
    }

    private Category findFlowCategory(CreateLineRequest request) {
        return categoryRepository.findFlowCategory(request.getFlow());
    }

    private Category findAssetCategory(CreateLineRequest request) {
        return categoryRepository.findAssetCategory(request.getAsset());
    }


}
