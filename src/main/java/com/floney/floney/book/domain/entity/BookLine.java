package com.floney.floney.book.domain.entity;

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

    @Builder
    private BookLine(final BookUser writer,
                     final Book book,
                     final BookLineCategory categories,
                     final Double money,
                     final LocalDate lineDate,
                     final String description,
                     final Boolean exceptStatus) {
        categories.setBookLine(this);

        this.writer = writer;
        this.book = book;
        this.categories = categories;
        this.money = money;
        this.lineDate = lineDate;
        this.description = description;
        this.exceptStatus = exceptStatus;
    }

    public static BookLine createByRepeatBookLine(LocalDate date, RepeatBookLine repeatBookLine) {
        return BookLine.builder()
            .categories(repeatBookLine.getCategories())
            .book(repeatBookLine.getBook())
            .description(repeatBookLine.getDescription())
            .lineDate(date)
            .money(repeatBookLine.getMoney())
            .exceptStatus(repeatBookLine.getExceptStatus())
            .writer(repeatBookLine.getWriter())
            .build();
    }

    public void update(BookLineRequest request) {
        this.money = request.getMoney();
        this.lineDate = request.getLineDate();
        this.description = request.getDescription();
        this.exceptStatus = request.getExcept();
    }

    public String getWriterNickName() {
        return this.writer.getNickName();
    }

    public void inactive() {
        Events.raise(new BookLineDeletedEvent(this.getId()));
        this.status = Status.INACTIVE;
    }

    public boolean includedInAsset() {
        return categories.isIncomeOrOutcome();
    }


}
