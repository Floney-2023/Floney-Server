package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
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

import static com.floney.floney.book.domain.category.CategoryType.*;

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

    public static CarryOver of(final BookLineRequest request, final Book book, final LocalDate date) {
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

    public static CarryOver of(final BookLine bookLine, final LocalDate date) {
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

    public static CarryOver init() {
        return CarryOver.builder()
            .money(0L)
            .build();
    }

    public void update(final double updateMoney, final CategoryType categoryType) {
        if (INCOME.equals(categoryType)) {
            money += updateMoney;
        } else {
            money -= updateMoney;
        }
    }

    public CarryOver delete(CategoryType categoryType) {
        if (TRANSFER.equals(categoryType)) {
            return CarryOver.builder()
                .money(this.money)
                .book(book)
                .date(date)
                .build();
        }

        return CarryOver.builder()
            .money(-1 * this.money)
            .book(book)
            .date(date)
            .build();
    }
}
