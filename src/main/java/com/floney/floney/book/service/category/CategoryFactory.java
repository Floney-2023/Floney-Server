package com.floney.floney.book.service.category;

import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookLineCategory;
import com.floney.floney.book.domain.entity.category.Category;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static com.floney.floney.book.dto.constant.CategoryEnum.*;
import static com.floney.floney.book.domain.entity.BookLineCategory.of;

@Component
@Getter
@RequiredArgsConstructor
public class CategoryFactory {
    private final CategoryRepository categoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    public void saveCategories(BookLine bookLine, BookLineRequest request) {
        String bookKey = request.getBookKey();
        bookLine.add(FLOW, saveFlowBookLineCategory(bookLine, request.getFlow()));
        bookLine.add(ASSET, saveAssetBookLineCategory(bookLine, request.getAsset(), bookKey));
        bookLine.add(FLOW_LINE, saveLineBookLineCategory(bookLine, request.getLine(), bookKey, request.getFlow()));
    }

    public void changeCategories(BookLine bookLine, BookLineRequest request) {
        String bookKey = request.getBookKey();
        bookLine.add(ASSET, changeAssetCategory(bookLine, request.getAsset(), bookKey));
        bookLine.add(FLOW_LINE, changeLineCategory(bookLine, bookKey, request.getLine()));
    }

    private BookLineCategory changeAssetCategory(BookLine bookLine, String requestCategory, String bookKey) {
        Map<CategoryEnum, BookLineCategory> categories = bookLine.getBookLineCategories();
        BookLineCategory currentCategory = categories.get(ASSET);
        if (!Objects.equals(currentCategory.getName(), requestCategory)) {
            //기존 카테고리 삭제
            currentCategory.inactive();
            bookLineCategoryRepository.save(currentCategory);

            return saveAssetBookLineCategory(bookLine, requestCategory, bookKey);
        }
        return currentCategory;
    }

    private BookLineCategory changeLineCategory(BookLine bookLine, String bookKey, String requestCategory) {
        Map<CategoryEnum, BookLineCategory> categories = bookLine.getBookLineCategories();
        BookLineCategory currentCategory = categories.get(FLOW_LINE);
        if (!Objects.equals(currentCategory.getName(), requestCategory)) {

            //기존 카테고리 삭제
            currentCategory.inactive();
            bookLineCategoryRepository.save(currentCategory);

            String flowCategory = categories.get(FLOW).getName();
            return saveLineBookLineCategory(bookLine, requestCategory, bookKey, flowCategory);
        }
        return currentCategory;
    }

    private BookLineCategory saveLineBookLineCategory(BookLine bookLine, String line, String bookKey, String flow) {
        Category category = findLineCategory(line, bookKey, flow);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookLineCategory saveFlowBookLineCategory(BookLine bookLine, String flow) {
        Category category = findFlowCategory(flow);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookLineCategory saveAssetBookLineCategory(BookLine bookLine, String asset, String bookKey) {
        Category category = findAssetCategory(asset, bookKey);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private Category findLineCategory(String line, String bookKey, String flow) {
        return categoryRepository.findLineCategory(line, bookKey, flow)
            .orElseThrow(() -> new NotFoundCategoryException(line));
    }

    private Category findFlowCategory(String flow) {
        return categoryRepository.findFlowCategory(flow)
            .orElseThrow(() -> new NotFoundCategoryException(flow));
    }

    private Category findAssetCategory(String asset, String bookKey) {
        return categoryRepository.findAssetCategory(asset, bookKey)
            .orElseThrow(() -> new NotFoundCategoryException(asset));
    }

}
