package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Asset extends BaseEntity {

    private double money;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    private LocalDate date;

    public static Asset of(final BookLine bookLine,
                           final CategoryType categoryType,
                           final LocalDate date) {
        if (OUTCOME.equals(categoryType)) {
            return Asset.builder()
                .money(-1 * bookLine.getMoney())
                .book(bookLine.getBook())
                .date(date)
                .build();
        }
        return Asset.builder()
            .money(bookLine.getMoney())
            .book(bookLine.getBook())
            .date(date)
            .build();
    }

    public void add(final double money, final String flow) {
        if (INCOME.getMeaning().equals(flow)) {
            this.money += money;
            return;
        }
        if (OUTCOME.getMeaning().equals(flow)) {
            this.money -= money;
            return;
        }
        // TODO: Exception 리팩토링 시 수정
        throw new RuntimeException("이체는 자산에 추가될 수 없습니다");
    }
}
