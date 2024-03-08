package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.common.entity.BaseEntity;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.book.domain.category.CategoryType.OUTCOME;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarryOver extends BaseEntity {

    private double money;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    private LocalDate date;

    @Builder
    @QueryProjection
    public CarryOver(double money, Book book, LocalDate date) {
        this.money = money;
        this.book = book;
        this.date = date;
    }

    public static CarryOver getCarryOverToAdd(final BookLine bookLine, final LocalDate date) {
        Category category = bookLine.getCategories().getLineCategory();
        if (OUTCOME.equals(category.getName())) {
            return CarryOver.builder()
                .money(-1 * bookLine.getMoney())
                .book(bookLine.getBook())
                .date(date)
                .build();
        }
        return CarryOver.builder()
            .money(bookLine.getMoney())
            .book(bookLine.getBook())
            .date(date)
            .build();
    }

    // 가계부 내역 삭제 시, 이월 금액에서 현재 내역을 취소 용
    public static CarryOver getCarryOverToDelete(final BookLine bookLine, final LocalDate date) {
        Category category = bookLine.getCategories().getLineCategory();
        if (INCOME.equals(category.getName())) {
            return CarryOver.builder()
                .money(-1 * bookLine.getMoney())
                .book(bookLine.getBook())
                .date(date)
                .build();
        }
        return CarryOver.builder()
            .money(bookLine.getMoney())
            .book(bookLine.getBook())
            .date(date)
            .build();
    }

    public static CarryOver init() {
        return CarryOver.builder()
            .money(0L)
            .build();
    }

}
