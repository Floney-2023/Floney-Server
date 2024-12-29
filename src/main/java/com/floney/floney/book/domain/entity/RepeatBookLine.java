package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.floney.floney.book.util.DateUtil.isWeekDay;
import static com.floney.floney.book.util.DateUtil.isWeekend;

@Entity
@Getter
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RepeatBookLine extends BaseEntity {

    public static final int REPEAT_YEAR = 1;

    @Enumerated(value = EnumType.STRING)
    private RepeatDuration repeatDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    private BookUser writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category lineCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subcategory lineSubcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subcategory assetSubcategory;

    @Column(nullable = false)
    private Double money;

    @Column(nullable = false)
    private LocalDate lineDate;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean exceptStatus;

    @Column
    @Lob
    private String memo;

    @Column
    private String imageUrl;

    public static RepeatBookLine of(BookLine bookLine, RepeatDuration repeatDuration) {
        BookLineCategory bookLineCategory = bookLine.getCategories();

        return RepeatBookLine.builder()
            .money(bookLine.getMoney())
            .book(bookLine.getBook())
            .writer(bookLine.getWriter())
            .description(bookLine.getDescription())
            .lineCategory(bookLineCategory.getLineCategory())
            .assetSubcategory(bookLineCategory.getAssetSubcategory())
            .lineSubcategory(bookLineCategory.getLineSubcategory())
            .lineDate(bookLine.getLineDate())
            .repeatDuration(repeatDuration)
            .exceptStatus(bookLine.getExceptStatus())
            .memo(bookLine.getMemo())
            .imageUrl(bookLine.getImageUrl())
            .build();
    }

    public List<BookLine> bookLinesBy(RepeatDuration repeatDuration) {
        List<BookLine> bookLines = new ArrayList<>();

        switch (repeatDuration) {
            case EVERYDAY -> {
                getEveryDay(bookLines);
            }
            case WEEK -> {
                getEveryWeek(bookLines);
            }
            case MONTH -> {
                getEveryMonth(bookLines);
            }
            case WEEKDAY -> {
                getEveryWeekDay(bookLines);
            }
            case WEEKEND -> {
                getEveryWeekend(bookLines);
            }
        }
        return bookLines;
    }

    private void getEveryDay(List<BookLine> bookLines) {
        //현재 날짜의 다음 날부터 반복 내역
        LocalDate startDate = this.getLineDate().plusDays(1);
        LocalDate endDate = this.getLineDate().plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            BookLine bookLine = BookLine.createByRepeatBookLine(date, this);
            bookLines.add(bookLine);
        }
    }

    private void getEveryMonth(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusMonths(1);
        LocalDate endDate = this.getLineDate().plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusMonths(1)) {
            BookLine bookLine = BookLine.createByRepeatBookLine(date, this);
            bookLines.add(bookLine);
        }
    }


    private void getEveryWeek(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusWeeks(1);
        LocalDate endDate = this.getLineDate().plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusWeeks(1)) {
            BookLine bookLine = BookLine.createByRepeatBookLine(date, this);
            bookLines.add(bookLine);
        }
    }

    private void getEveryWeekend(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusDays(1);
        LocalDate endDate = this.getLineDate().plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (isWeekend(date)) {
                bookLines.add(BookLine.createByRepeatBookLine(date, this));
            }
        }
    }

    private void getEveryWeekDay(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusDays(1);
        LocalDate endDate = this.getLineDate().plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (isWeekDay(date)) {
                bookLines.add(BookLine.createByRepeatBookLine(date, this));
            }
        }
    }


}
