package com.floney.floney.book.dto.process;

import com.floney.floney.book.domain.category.CategoryType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DayLines {

    private final Long id;
    private final double money;
    private final String img;
    private final List<String> category;
    private final CategoryType assetType;
    private final String content;
    private final String userEmail;
    private final boolean exceptStatus;
    private final String userNickName;

    public static DayLines from(final BookLineWithWriterView bookLineWithWriterView) {
        return DayLines.builder()
            .id(bookLineWithWriterView.getId())
            .money(bookLineWithWriterView.getMoney())
            .content(bookLineWithWriterView.getDescription())
            .exceptStatus(bookLineWithWriterView.isExceptStatus())
            .assetType(bookLineWithWriterView.getLineCategory())
            .category(List.of(bookLineWithWriterView.getLineSubCategory(), bookLineWithWriterView.getAssetSubCategory()))
            .userEmail(bookLineWithWriterView.getWriterEmail())
            .img(bookLineWithWriterView.getWriterProfileImg())
            .userNickName(bookLineWithWriterView.getWriterNickname())
            .build();
    }
}
