package com.floney.floney.book.entity;

import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookAnalyze extends BaseEntity {
    private Long totalMoney;
    @OneToOne
    private Category category;
    @ManyToOne
    private Book book;

    private LocalDate analyzeDate;

    @Builder
    public BookAnalyze(Long totalMoney, Category category, Book book, LocalDate analyzeDate) {
        this.totalMoney = totalMoney;
        this.category = category;
        this.book = book;
        this.analyzeDate = analyzeDate;
    }
}
