package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CategoryInfo {
    private boolean isDefault;

    private String name;

    @QueryProjection
    @Builder
    public CategoryInfo(boolean isDefault, String name) {
        this.isDefault = isDefault;
        this.name = name;
    }

}
