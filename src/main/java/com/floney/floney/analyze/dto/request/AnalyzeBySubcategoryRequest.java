package com.floney.floney.analyze.dto.request;

import com.floney.floney.analyze.domain.BookLineSortingType;
import lombok.*;

import java.time.YearMonth;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeBySubcategoryRequest {

    private String bookKey;
    private String subcategory;
    private List<Long> userIds;
    private BookLineSortingType sortingType;
    private YearMonth yearMonth;
}
