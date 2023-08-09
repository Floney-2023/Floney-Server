package com.floney.floney.book.entity;

import com.floney.floney.book.dto.response.AnalyzeResponseByCategory;
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
import java.util.List;

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
    private BookAnalyze(List<AnalyzeResponseByCategory> analyzeResult, Category category, Book book, LocalDate analyzeDate) {
        this.totalMoney = calculateTotalMoney(analyzeResult);
        this.category = category;
        this.book = book;
        this.analyzeDate = analyzeDate;
    }

    public Long calculateDifferenceWith(Long beforeMonthMoney) {
        return this.totalMoney - beforeMonthMoney;
    }

    private Long calculateTotalMoney(List<AnalyzeResponseByCategory> result) {
        return result.stream()
            .mapToLong(AnalyzeResponseByCategory::getMoney)
            .sum();
    }
}
