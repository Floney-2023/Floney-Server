package com.floney.floney.analyze.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AnalyzeByCategoryRequest {

    // TODO: validation 추가
    private String bookKey;
    private String root;
    private String date; // TODO: LocalDate 형으로 받기

    public LocalDate getLocalDate() {
        return LocalDate.parse(date);
    }
}
