package com.floney.floney.book.dto;

import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

@RequiredArgsConstructor
public class CategoryCreator {
    private final CategoryRepository categoryRepository;

    public BookLineCategory create(CarryOverInfo info) {
        if (info.getAssetType() == INCOME) {
            return inComeCategory(info.getBookLine());
        } else {
            return outComeCategory(info.getBookLine());
        }
    }

    private BookLineCategory inComeCategory(BookLine line) {
        return BookLineCategory.builder()
            .bookLine(line)
            .name(INCOME.name())
            .category(findCategory(INCOME.name()))
            .build();
    }


    private BookLineCategory outComeCategory(BookLine line) {
        return BookLineCategory.builder()
            .bookLine(line)
            .name(OUTCOME.name())
            .category(findCategory(OUTCOME.name()))
            .build();

    }

    private Category findCategory(String category) {
        return categoryRepository.findAssetCategory(category);
    }
}
