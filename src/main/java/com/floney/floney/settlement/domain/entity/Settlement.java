package com.floney.floney.settlement.domain.entity;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.settlement.dto.request.OutcomeRequest;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id")
    private List<SettlementUser> details;

    @Column(nullable = false, updatable = false)
    private LocalDate startDate;

    @Column(nullable = false, updatable = false)
    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private Integer userCount;

    @Column(nullable = false, updatable = false)
    private float totalOutcome;

    @Column(nullable = false, updatable = false)
    private float avgOutcome;

    @QueryProjection
    private Settlement(Book book, List<SettlementUser> details, LocalDate startDate, LocalDate endDate, Integer userCount, float totalOutcome, float avgOutcome) {
        this.book = book;
        this.details = details;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userCount = userCount;
        this.totalOutcome = totalOutcome;
        this.avgOutcome = avgOutcome;
    }

    public static Settlement of(Book book, SettlementRequest request) {
        final Integer userCount = calculateUserCount(request.getUserEmails());
        final float totalOutcome = (float) calculateTotalOutcome(request.getOutcomes());
        final float avgOutcome = calculateAvgOutcome(totalOutcome, userCount);

        return Settlement.builder()
                .book(book)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .userCount(userCount)
                .totalOutcome(totalOutcome)
                .avgOutcome(avgOutcome)
                .build();
    }

    private static Integer calculateUserCount(Set<String> userEmails) {
        return userEmails.size();
    }

    private static Long calculateTotalOutcome(List<OutcomeRequest> request) {
        return request.stream()
                .mapToLong(OutcomeRequest::getOutcome)
                .sum();
    }

    private static float calculateAvgOutcome(float totalOutcome, Integer userCount) {
        final BigDecimal dividend = BigDecimal.valueOf(totalOutcome);
        final BigDecimal divisor = BigDecimal.valueOf(userCount);
        return dividend.divide(divisor, RoundingMode.HALF_UP).longValue();
    }

    public void updateBookLastSettlementDate() {
        book.updateLastSettlementDate(getCreatedAt().toLocalDate());
    }

}
