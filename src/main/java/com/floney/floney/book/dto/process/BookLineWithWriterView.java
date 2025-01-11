package com.floney.floney.book.dto.process;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.response.BookLineImgResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class BookLineWithWriterView {

    private final long id;

    private final double money;

    private final String description;

    private final boolean exceptStatus;

    private final CategoryType lineCategory;

    private final String lineSubCategory;

    private final String assetSubCategory;

    private final String writerEmail;

    private final String writerNickname;

    private final String writerProfileImg;

    private final RepeatDuration repeatDuration;

    private final String memo;

    private List<BookLineImgResponse> imageUrls;

    @Builder
    @QueryProjection
    public BookLineWithWriterView(final long id,
                                  final double money,
                                  final String description,
                                  final boolean exceptStatus,
                                  final CategoryType lineCategory,
                                  final String lineSubCategory,
                                  final String assetSubCategory,
                                  final String writerEmail,
                                  final String writerNickname,
                                  final String writerProfileImg,
                                  final RepeatDuration repeatDuration,
                                  final String memo) {
        this.id = id;
        this.money = money;
        this.description = description;
        this.writerProfileImg = writerProfileImg;
        this.exceptStatus = exceptStatus;
        this.lineCategory = lineCategory;
        this.lineSubCategory = lineSubCategory;
        this.assetSubCategory = assetSubCategory;
        this.writerEmail = writerEmail;
        this.writerNickname = writerNickname;
        this.repeatDuration = repeatDuration;
        this.memo = memo;
    }

    public void updateImgUrls(final List<BookLineImgResponse> imgUrls) {
        this.imageUrls = imgUrls;
    }
}
