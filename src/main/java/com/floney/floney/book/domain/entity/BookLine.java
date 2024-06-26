package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.event.BookLineDeletedEvent;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.util.Events;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookLine extends BaseEntity {

    public static final LocalDate START_DATE = LocalDate.of(2000, 1, 1);

    @ManyToOne(fetch = FetchType.LAZY)
    private BookUser writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @OneToOne(mappedBy = "bookLine", cascade = CascadeType.ALL)
    private BookLineCategory categories;

    @Column(nullable = false)
    private Double money;

    @Column(nullable = false)
    private LocalDate lineDate;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean exceptStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RepeatBookLine repeatBookLine;

    @Builder
    private BookLine(final BookUser writer,
                     final Book book,
                     final BookLineCategory categories,
                     final Double money,
                     final LocalDate lineDate,
                     final String description,
                     final Boolean exceptStatus,
                     final RepeatBookLine repeatBookLine) {

        categories.setBookLine(this);

        this.writer = writer;
        this.book = book;
        this.categories = categories;
        this.money = money;
        this.lineDate = lineDate;
        this.description = description;
        this.exceptStatus = exceptStatus;
        this.repeatBookLine = repeatBookLine;
    }

    public static BookLine createByRepeatBookLine(LocalDate date, RepeatBookLine repeatBookLine) {
        BookLineCategory bookLineCategory = BookLineCategory.create(repeatBookLine.getLineCategory(), repeatBookLine.getLineSubcategory(), repeatBookLine.getAssetSubcategory());

        return BookLine.builder()
            .categories(bookLineCategory)
            .book(repeatBookLine.getBook())
            .description(repeatBookLine.getDescription())
            .lineDate(date)
            .money(repeatBookLine.getMoney())
            .exceptStatus(repeatBookLine.getExceptStatus())
            .writer(repeatBookLine.getWriter())
            .repeatBookLine(repeatBookLine)
            .build();
    }

    public void update(BookLineRequest request) {
        this.money = request.getMoney();
        this.lineDate = request.getLineDate();
        this.description = request.getDescription();
        this.exceptStatus = request.getExcept();
    }

    public RepeatDuration getRepeatDuration() {
        if (this.repeatBookLine != null) {
            return this.repeatBookLine.getRepeatDuration();
        }
        return RepeatDuration.NONE;
    }

    public boolean isIncludedInAsset() {
        return (this.isIncome() && !exceptStatus) || this.isOutcome();
    }

    public String getWriterNickName() {
        return this.writer.getNickName();
    }

    public void repeatBookLine(RepeatBookLine repeatBookLine) {
        this.repeatBookLine = repeatBookLine;
    }

    public void inactive() {
        Events.raise(new BookLineDeletedEvent(this.getId()));
        this.status = Status.INACTIVE;
    }

    public boolean isNotRepeat() {
        return this.repeatBookLine == null;
    }

    public boolean isIncome() {
        return categories.isIncome();
    }

    public boolean isOutcome() {
        return categories.isOutcome();
    }

    public void updateDate(final LocalDate date) {
        this.lineDate = date;
    }
}
