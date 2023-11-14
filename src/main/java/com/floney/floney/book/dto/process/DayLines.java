package com.floney.floney.book.dto.process;

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
    private final Long id;
    private final float money;
    private final String img;
    private final List<String> category;
    private final AssetType assetType;
    private final String content;
    private final String userEmail;
    private final boolean exceptStatus;
    private final String userNickName;

    @Builder
    public DayLines(Long id, float money, String img, String content, List<String> category, AssetType assetType, String userEmail, boolean exceptStatus, String userNickName) {
        this.id = id;
        this.money = money;
        this.img = img;
        this.assetType = assetType;
        this.category = category;
        this.content = content;
        this.userEmail = userEmail;
        this.exceptStatus = exceptStatus;
        this.userNickName = userNickName;
    }

    // 가계부 내역에 여러개의 카테고리를 하나의 객체로 합치는 함수
    public static List<DayLines> forDayView(List<DayLineByDayView> dayLines) {
        Map<Long, DayLineInfo> dayLineWithCategories = new HashMap<>();

        dayLines.forEach((dayLine) ->
        {
            DayLineInfo dayLineInfo = dayLineWithCategories.get(dayLine.getId());

            // 카테고리 외의 데이터 최초 등록
            if (dayLineInfo == null) {
                dayLineWithCategories.put(dayLine.getId(), DayLineInfo.toDayViewInfos(dayLine));
            } else {
                dayLineInfo.addCategory(dayLine.getCategories());
            }
        });

        // 최종 응답 변경
        return dayLineWithCategories.values()
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
            .map(DayLines::toOutComeResponse)
            .collect(Collectors.toList());

    }

    private static DayLines toOutComeResponse(DayLineInfo dayInfo) {
        return DayLines.builder()
            .assetType(dayInfo.getAssetType())
            .money(dayInfo.getMoney())
            .content(dayInfo.getContent())
            .category(dayInfo.getCategories())
            .img(dayInfo.getImg())
            .userEmail(dayInfo.getUserEmail())
            .userNickName(dayInfo.getUserNickName())
            .exceptStatus(dayInfo.isExceptStatus())
            .build();
    }


    private static DayLines toDayLineResponse(DayLineInfo dayInfo) {
        return DayLines.builder()
            .id(dayInfo.getId())
            .assetType(dayInfo.getAssetType())
            .money(dayInfo.getMoney())
            .content(dayInfo.getContent())
            .category(dayInfo.getCategories())
            .img(dayInfo.getImg())
            .userEmail(dayInfo.getUserEmail())
            .userNickName(dayInfo.getUserNickName())
            .exceptStatus(dayInfo.isExceptStatus())
            .build();
    }

}
