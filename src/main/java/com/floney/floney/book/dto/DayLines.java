package com.floney.floney.book.dto;

import com.floney.floney.book.dto.constant.AssetType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class DayLines {

    private Long money;
    private String img;
    private List<String> category;
    private AssetType assetType;
    private String content;

    @Builder
    public DayLines(Long money, String img, String content, List<String> category, AssetType assetType) {
        this.money = money;
        this.img = img;
        this.assetType = assetType;
        this.category = category;
        this.content = content;
    }

    public static List<DayLines> of(List<DayLine> dayLines) {
        Map<Long, DayLineInfo> InfosByDay = new HashMap<>();
        for (DayLine dayLine : dayLines) {
            if (InfosByDay.get(dayLine.getId()) == null) {
                InfosByDay.put(dayLine.getId(), DayLineInfo.toDayInfos(dayLine));
            } else {
                InfosByDay.get(dayLine.getId()).addCategory(dayLine.getCategories());
            }
        }
        return InfosByDay.values()
            .stream()
            .map(DayLines::toDayLineResponse)
            .collect(Collectors.toList());
    }

    private static DayLines toDayLineResponse(DayLineInfo dayInfo) {
        return DayLines.builder()
            .assetType(dayInfo.getAssetType())
            .money(dayInfo.getMoney())
            .content(dayInfo.getContent())
            .category(dayInfo.getCategories())
            .img(dayInfo.getImg())
            .build();
    }

}
