package com.floney.floney.analyze.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyzeResponseBySubcategory {
    private final String subcategoryName;
    private final List<BookLineResponse> bookLines;

    public static AnalyzeResponseBySubcategory of(final String subcategoryName,
                                                  final List<BookLineResponse> bookLines) {
        return new AnalyzeResponseBySubcategory(subcategoryName, bookLines);
    }
}
