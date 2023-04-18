package com.floney.floney.book.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Categories {

    private LineCategory lineCategory;

    private AssetCategory assetCategory;

    private RepeatCategory repeatCategory;

    private FlowCategory flowCategory;

    @Builder
    private Categories(LineCategory lineCategory, AssetCategory assetCategory, RepeatCategory repeatCategory, FlowCategory flowCategory) {
        this.lineCategory = lineCategory;
        this.assetCategory = assetCategory;
        this.repeatCategory = repeatCategory;
        this.flowCategory = flowCategory;
    }

    public static Categories to(LineCategory lineCategory, AssetCategory assetCategory, RepeatCategory repeatCategory, FlowCategory flowCategory) {
        return Categories.builder()
            .assetCategory(assetCategory)
            .flowCategory(flowCategory)
            .lineCategory(lineCategory)
            .repeatCategory(repeatCategory)
            .build();
    }
}
