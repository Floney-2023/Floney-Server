package com.floney.floney.book.entity;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.entity.BaseEntity;
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
import java.time.LocalDateTime;
import java.util.Objects;

import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asset extends BaseEntity {
    private Float money;
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    private LocalDate date;

    @Builder
    private Asset(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Status status, Float money, Book book, LocalDate date) {
        super(id, createdAt, updatedAt, status);
        this.money = money;
        this.book = book;
        this.date = date;
    }

    public static Asset of(ChangeBookLineRequest request, Book book, LocalDate date) {
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

    public void update(float updateMoney, String flow) {
        if (Objects.equals(flow, AssetType.INCOME.getKind())) {
            money += updateMoney;
        } else {
            money -= updateMoney;
        }
    }

    public void delete(float updateMoney, BookLineCategory flow){
        // 기존 내역이 수입이였다면, 현 자산에서 감소
        if (Objects.equals(flow.getName(), AssetType.INCOME.getKind())) {
            money -= updateMoney;
        }

        // 기존 내역이 지출이였다면, 현 자산에 합
        else if (Objects.equals(flow.getName(), AssetType.OUTCOME.getKind())){
            money += updateMoney;
        }
    }
}
