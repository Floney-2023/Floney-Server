package com.floney.floney.book.dto;

import com.floney.floney.book.dto.constant.AssetType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DayLineInfo {
    private Long money;

    private String content;

    private AssetType assetType;

    private List<String> categories;

    private String img;

    @Builder
    public DayLineInfo(Long money, String assetType, String content, List<String> categories, String img) {
        this.money = money;
        this.content = content;
        this.assetType = AssetType.find(assetType);
        this.categories = categories;
        this.img = img;
    }

    public void addCategory(String category){
        this.categories.add(category);
    }

    public static DayLineInfo toDayInfos(DayLine dayLine){
        return DayLineInfo.builder()
            .assetType(dayLine.getCategories())
            .categories(new ArrayList<>())
            .money(dayLine.getMoney())
            .content(dayLine.getContent())
            .img(dayLine.getProfileImg())
            .build();
    }
}
