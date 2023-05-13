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
public class DayLinesResponse {

    private Long money;
    private String img;
    private List<String> category;
    private AssetType assetType;
    private String content;

    @Builder
    public DayLinesResponse(Long money, String img, String content, List<String> category, AssetType assetType) {
        this.money = money;
        this.img = img;
        this.assetType = assetType;
        this.category = category;
        this.content = content;
    }

    public static List<DayLinesResponse> of(List<DayLine> dayLines) {
        Map<Long, DayInfos> InfosByDay = new HashMap<>();
        for (DayLine dayLine : dayLines) {
            if (InfosByDay.get(dayLine.getId()) == null) {
                InfosByDay.put(dayLine.getId(), DayInfos.toDayInfos(dayLine));
            } else {
                InfosByDay.get(dayLine.getId()).addCategory(dayLine.getCategories());
            }
        }
        return InfosByDay.values().stream()
            .map(DayLinesResponse::toDayLineResponse)
            .collect(Collectors.toList());
    }

    private static DayLinesResponse toDayLineResponse(DayInfos dayInfo) {
        return DayLinesResponse.builder()
            .assetType(dayInfo.getAssetType())
            .money(dayInfo.getMoney())
            .content(dayInfo.getContent())
            .category(dayInfo.getCategories())
            .img(dayInfo.getImg())
            .build();
    }

}
