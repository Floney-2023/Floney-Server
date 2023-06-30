package com.floney.floney.book.entity;

import com.floney.floney.book.dto.CarryOverInfo;
import com.floney.floney.book.dto.constant.AssetType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static java.lang.Math.abs;

@Getter
@Service
@RequiredArgsConstructor
public class CarryOver {
    private static final int STANDARD = 0;
    private static final String CARRY_OVER_DESCRIPTION = "이월";

    private Book book;
    private Long income;
    private Long outcome;

    @Builder
    public CarryOver(Book book, Long income, Long outcome) {
        this.book = book;
        this.income = income;
        this.outcome = outcome;
    }

    public CarryOverInfo calculateValue() {
        long total = income - outcome;

        Hibernate.initialize(book);
        BookLine bookLine = getBookLine(total);

        if (total < STANDARD) {
            return CarryOverInfo.builder()
                .assetType(AssetType.OUTCOME)
                .bookLine(bookLine)
                .build();
        } else if (total > STANDARD) {
            return CarryOverInfo.builder()
                .assetType(AssetType.INCOME)
                .bookLine(bookLine)
                .build();
        } else {
            return null;
        }
    }

    private BookLine getBookLine(long total) {
        return BookLine.builder()
            .book(book)
            .money(abs(total))
            .exceptStatus(false)
            .description(CARRY_OVER_DESCRIPTION)
            .lineDate(LocalDate.now())
            .build();
    }
}
