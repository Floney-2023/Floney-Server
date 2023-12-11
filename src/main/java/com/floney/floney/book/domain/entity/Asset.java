package com.floney.floney.book.domain.entity;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;

import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

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

    public static Asset of(BookLineRequest request, Book book, LocalDate date) {
        if (Objects.equals(request.getFlow(), OUTCOME.getKind())) {
            return Asset
                    .builder()
                    .money(-1 * request.getMoney())
                    .book(book)
                    .date(date)
                    .build();
        } else {
            return Asset
                    .builder()
                    .money(request.getMoney())
                    .book(book)
                    .date(date)
                    .build();
        }
    }

    public void add(double updateMoney, String flow) {
        if (Objects.equals(flow, AssetType.INCOME.getKind())) {
            money += updateMoney;
        } else {
            money -= updateMoney;
        }
    }

    public void subtract(double updateMoney, BookLineCategory flow) {
        // 기존 내역이 수입이였다면, 현 자산에서 감소
        if (Objects.equals(flow.getName(), AssetType.INCOME.getKind())) {
            money -= updateMoney;
        }

        // 기존 내역이 지출이였다면, 현 자산에 합
        else if (Objects.equals(flow.getName(), AssetType.OUTCOME.getKind())) {
            money += updateMoney;
        }
    }
}
