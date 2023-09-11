package com.floney.floney.book.entity;

import com.floney.floney.book.dto.constant.AssetType;
import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.util.DateFactory;
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
import java.util.Objects;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CarryOver extends BaseEntity {

    private long money;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    private LocalDate date;

    @Builder
    private CarryOver(long money, Book book, LocalDate date) {
        this.money = money;
        this.book = book;
        this.date = date;
    }

    public static CarryOver of(CreateLineRequest request, Book book, LocalDate date) {
        long money = request.getMoney();
        if(Objects.equals(request.getFlow(), AssetType.OUTCOME.name())){
            money = -1 * request.getMoney();
        }
        return CarryOver
            .builder()
            .money(money)
            .book(book)
            .date(date)
            .build();
    }

    public void update(Long updateMoney, String flow) {
        if(Objects.equals(flow, AssetType.INCOME.name())){
            money += updateMoney;
        }
        else{
            money -= updateMoney;
        }
    }
}
