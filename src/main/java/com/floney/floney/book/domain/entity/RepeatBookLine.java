package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RepeatBookLine extends BaseEntity {

    public static final int REPEAT_YEAR = 5;


    @Enumerated(value = EnumType.STRING)
    private RepeatDuration repeatDuration;

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

    public static RepeatBookLine of(BookLine bookLine, Book book, RepeatDuration repeatDuration) {
        return RepeatBookLine.builder()
            .money(bookLine.getMoney())
            .book(book)
            .writer(bookLine.getWriter())
            .description(bookLine.getDescription())
            .categories(bookLine.getCategories())
            .lineDate(bookLine.getLineDate())
            .repeatDuration(repeatDuration)
            .exceptStatus(bookLine.getExceptStatus())
            .build();
    }

    public List<BookLine> bookLinesByRepeat(RepeatDuration repeatDuration) {
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
                getWeekDay(bookLines);
            }
            case WEEKEND -> {
                getEveryWeekend(bookLines);
            }
        }
        return bookLines;
    }

    private void getEveryDay(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusDays(1);
        LocalDate endDate = startDate.plusYears(REPEAT_YEAR);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            BookLine bookLine = BookLine.of(date, this);
            bookLines.add(bookLine);
        }

    }

    private void getEveryMonth(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusMonths(1);
        LocalDate endDate = startDate.plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {
            BookLine bookLine = BookLine.of(date, this);
            bookLines.add(bookLine);
        }
    }


    private void getEveryWeek(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusWeeks(1);
        LocalDate endDate = startDate.plusYears(REPEAT_YEAR);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusWeeks(1)) {
            BookLine bookLine = BookLine.of(date, this);
            bookLines.add(bookLine);
        }

    }

    private void getEveryWeekend(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusDays(1);
        LocalDate endDate = startDate.plusYears(REPEAT_YEAR);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            // 주중인지 확인
            if (!isWeekDay(date)) {
                bookLines.add(BookLine.of(date, this));
            }
        }
    }

    private void getWeekDay(List<BookLine> bookLines) {
        LocalDate startDate = this.getLineDate().plusDays(1);
        LocalDate endDate = startDate.plusYears(REPEAT_YEAR);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            // 주중인지 확인
            if (isWeekDay(date)) {
                bookLines.add(BookLine.of(date, this));
            }
        }
    }

    private boolean isWeekDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }


}
