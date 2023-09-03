package com.floney.floney.settlement.domain.entity;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.settlement.dto.request.OutcomeRequest;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
    private Long totalOutcome;

    @Column(nullable = false, updatable = false)
    private Long avgOutcome;

    @QueryProjection
    private Settlement(Book book, List<SettlementUser> details, LocalDate startDate, LocalDate endDate, Integer userCount, Long totalOutcome, Long avgOutcome) {
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
        final Long totalOutcome = calculateTotalOutcome(request.getOutcomes());
        final Long avgOutcome = calculateAvgOutcome(totalOutcome, userCount);

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

    private static Long calculateAvgOutcome(Long totalOutcome, Integer userCount) {
        final BigDecimal dividend = BigDecimal.valueOf(totalOutcome);
        final BigDecimal divisor = BigDecimal.valueOf(userCount);
        return dividend.divide(divisor, RoundingMode.HALF_UP).longValue();
    }

    public void updateBookLastSettlementDate() {
        book.updateLastSettlementDate(getCreatedAt().toLocalDate());
    }

    public Settlement deleteForever() {
        this.book = null;
        return this;
    }
}
