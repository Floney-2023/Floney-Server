package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static com.floney.floney.book.dto.constant.CategoryEnum.*;
import static com.floney.floney.book.entity.BookLineCategory.of;

@Component
@Getter
@RequiredArgsConstructor
public class CategoryFactory {
    private final CategoryRepository categoryRepository;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    public void saveCategories(BookLine bookLine, CreateLineRequest request) {
        bookLine.add(FLOW, saveFlowBookLineCategory(bookLine, request.getFlow()));
        bookLine.add(ASSET, saveAssetBookLineCategory(bookLine, request.getAsset()));
        bookLine.add(FLOW_LINE, saveLineBookLineCategory(bookLine, request.getLine(), request.getBookKey(), request.getFlow()));
    }

    public void changeCategories(BookLine bookLine, ChangeBookLineRequest request) {
        bookLine.add(FLOW, changeFlowCategory(bookLine, request.getFlow()));
        bookLine.add(ASSET, changeAssetCategory(bookLine, request.getAsset()));
        bookLine.add(FLOW_LINE, changeLineCategory(bookLine, request.getBookKey(), request.getLine()));
    }

    private BookLineCategory changeFlowCategory(BookLine bookLine, String requestCategory) {
        Map<CategoryEnum, BookLineCategory> categories = bookLine.getBookLineCategories();
        BookLineCategory currentCategory = categories.get(CategoryEnum.FLOW);
        if (!Objects.equals(currentCategory.getName(), requestCategory)) {
            return saveFlowBookLineCategory(bookLine, requestCategory);
        }
        return currentCategory;
    }

    private BookLineCategory changeAssetCategory(BookLine bookLine, String requestCategory) {
        Map<CategoryEnum, BookLineCategory> categories = bookLine.getBookLineCategories();
        BookLineCategory currentCategory = categories.get(ASSET);
        if (!Objects.equals(currentCategory.getName(), requestCategory)) {
            return saveAssetBookLineCategory(bookLine, requestCategory);
        }
        return currentCategory;
    }

    private BookLineCategory changeLineCategory(BookLine bookLine, String bookKey, String requestCategory) {
        Map<CategoryEnum, BookLineCategory> categories = bookLine.getBookLineCategories();
        BookLineCategory currentCategory = categories.get(FLOW_LINE);
        if (!Objects.equals(currentCategory.getName(), requestCategory)) {
            String flowCategory = bookLine.getBookLineCategories()
                .get(FLOW)
                .getName();
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

    private BookLineCategory saveAssetBookLineCategory(BookLine bookLine, String asset) {
        Category category = findAssetCategory(asset);
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

    private Category findAssetCategory(String asset) {
        return categoryRepository.findAssetCategory(asset);
    }

}
