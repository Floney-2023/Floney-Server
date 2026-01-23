package com.floney.floney.book.dto.process;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryInfo {

    private boolean isDefault; // TODO: 삭제
    private String name;
    private String categoryKey;

    @Builder
    @QueryProjection
    public CategoryInfo(final boolean isDefault, final String name, final String categoryKey) {
        this.isDefault = isDefault;
        this.name = name;
        this.categoryKey = categoryKey;
    }
}
