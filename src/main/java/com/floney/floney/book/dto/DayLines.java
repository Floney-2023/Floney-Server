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

    private final Long money;
    private final String img;
    private final List<String> category;
    private final AssetType assetType;
    private final String content;

    @Builder
    public DayLines(Long money, String img, String content, List<String> category, AssetType assetType) {
        this.money = money;
        this.img = img;
        this.assetType = assetType;
        this.category = category;
        this.content = content;
    }

    public static List<DayLines> forDayView(List<DayLineByDayView> dayLines) {
        Map<Long, DayLineInfo> infosByDay = new HashMap<>();
        for (DayLineByDayView dayLine : dayLines) {
            if (infosByDay.get(dayLine.getId()) == null) {
                infosByDay.put(dayLine.getId(), DayLineInfo.toDayViewInfos(dayLine));
            } else {
                infosByDay.get(dayLine.getId()).addCategory(dayLine.getCategories());
            }
        }

        return infosByDay.values()
            .stream()
            .map(DayLines::toDayLineResponse)
            .collect(Collectors.toList());
    }

    public static List<DayLines> forOutcomes(List<DayLine> dayLines) {
        Map<Long, DayLineInfo> infosByDay = new HashMap<>();

        for (DayLine dayLine : dayLines) {
            DayLineInfo info = infosByDay.computeIfAbsent(dayLine.getId(), id -> DayLineInfo.toDayInfos(dayLine));
            info.addCategory(dayLine.getCategories());
        }

        return infosByDay.values()
            .stream()
            .filter(dayLineInfo -> dayLineInfo.getAssetType() == AssetType.OUTCOME)
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
