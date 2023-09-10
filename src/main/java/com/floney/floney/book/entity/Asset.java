package com.floney.floney.book.entity;

import com.floney.floney.book.dto.request.UpdateAssetRequest;
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

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asset extends BaseEntity {

    private LocalDate date;

    private Long money;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @Builder
    private Asset(LocalDate date, Long money, Book book) {
        this.date = date;
        this.money = money;
        this.book = book;
    }

    public static Asset of(Book book, UpdateAssetRequest request) {
        return Asset.builder()
            .money(request.getAsset())
            .date(request.getDate())
            .book(book)
            .build();
    }

    public static Asset init() {
        return Asset.builder()
            .money(0L)
            .build();
    }


    public void update(UpdateAssetRequest request) {
        this.money = request.getAsset();
    }
}
