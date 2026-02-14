package com.floney.floney.analyze.dto.request;

import com.floney.floney.analyze.domain.BookLineSortingType;
import com.floney.floney.book.domain.category.CategoryType;
import lombok.*;

import java.time.YearMonth;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeBySubcategoryRequest {

    private String bookKey;
    private CategoryType category;
    private String subcategory;  // Accepts both categoryKey (for default) or name (for user-defined)
    private List<String> emails;
    private BookLineSortingType sortingType;
    private YearMonth yearMonth;
}
