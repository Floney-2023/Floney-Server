package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.constant.AssetType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DayLineInfo {
    private final Long id;

    private final Long money;

    private final String content;

    private final AssetType assetType;

    private final List<String> categories;

    private final String img;

    private final String userEmail;

    @Builder
    public DayLineInfo(Long id, Long money, String assetType, String content, List<String> categories, String img, String userEmail) {
        this.id = id;
        this.money = money;
        this.content = content;
        this.assetType = AssetType.find(assetType);
        this.categories = categories;
        this.img = img;
        this.userEmail = userEmail;
    }

    public void addCategory(String category) {
        if (AssetType.isAssetType(category)) {
            return;
        }
        this.categories.add(category);
    }

    public static DayLineInfo toDayViewInfos(DayLineByDayView dayLine) {
        return DayLineInfo.builder()
            .id(dayLine.getId())
            .assetType(dayLine.getCategories())
            .categories(new ArrayList<>())
            .money(dayLine.getMoney())
            .content(dayLine.getContent())
            .img(dayLine.getProfileImg())
            .build();
    }

    public static DayLineInfo toDayInfos(DayLine dayLine) {
        return DayLineInfo.builder()
            .assetType(dayLine.getCategories())
            .categories(new ArrayList<>())
            .money(dayLine.getMoney())
            .content(dayLine.getContent())
            .userEmail(dayLine.getUserEmail())
            .build();
    }
}
