package com.floney.floney.book.domain.entity;

import com.floney.floney.book.dto.request.BookLineRequest;
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

    public static CarryOver of(final BookLineRequest request,
                               final Book book,
                               final LocalDate date) {
        if (OUTCOME.getMeaning().equals(request.getFlow())) {
            return CarryOver.builder()
                .money(-1 * request.getMoney())
                .book(book)
                .date(date)
                .build();
        }
        return CarryOver.builder()
            .money(request.getMoney())
            .book(book)
            .date(date)
            .build();
    }

    public static CarryOver init() {
        return CarryOver.builder()
            .money(0L)
            .build();
    }

    public void update(final double updateMoney, final String lineCategoryName) {
        if (INCOME.getMeaning().equals(lineCategoryName)) {
            money += updateMoney;
        } else {
            money -= updateMoney;
        }
    }

    // 내역을 삭제하는 경우, 이월된 값을 되돌리기
    public void delete(final double updateMoney, final BookLineCategory bookLineCategory) {
        if (!bookLineCategory.isIncomeOrOutcome()) return;
        if (bookLineCategory.isIncome()) {
            money -= updateMoney;
        }
        money += updateMoney;
    }
}
