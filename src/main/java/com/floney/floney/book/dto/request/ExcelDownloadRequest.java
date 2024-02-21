package com.floney.floney.book.dto.request;

import com.floney.floney.book.domain.ExcelDuration;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ExcelDownloadRequest {
    private String bookKey;
    private ExcelDuration excelDuration;
    private String currentDate;

    @Builder
    private ExcelDownloadRequest(final String bookKey, final ExcelDuration excelDuration, final String currentDate) {
        this.bookKey = bookKey;
        this.excelDuration = excelDuration;
        this.currentDate = currentDate;
    }
}

