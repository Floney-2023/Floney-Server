package com.floney.floney.book.util;

import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

@RequiredArgsConstructor
@Component
public class CategoryFactory {
    private final CategoryRepository categoryRepository;

    public BookLineCategory create(CarryOverInfo info) {
        if (info.getAssetType() == INCOME) {
            return incomeCategory(info.getBookLine());
        } else {
            return outcomeCategory(info.getBookLine());
        }
    }

    private BookLineCategory incomeCategory(BookLine line) {
        return BookLineCategory.builder()
            .bookLine(line)
            .name(INCOME.getKind())
            .category(findCategory(INCOME.getKind()))
            .build();
    }


    private BookLineCategory outcomeCategory(BookLine line) {
        return BookLineCategory.builder()
            .bookLine(line)
            .name(OUTCOME.getKind())
            .category(findCategory(OUTCOME.getKind()))
            .build();

    }

    private Category findCategory(String category) {
        return categoryRepository.findFlowCategory(category);
    }
}
