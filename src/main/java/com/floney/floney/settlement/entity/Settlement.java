package com.floney.floney.settlement.entity;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.settlement.dto.request.OutcomeRequest;
import com.floney.floney.settlement.dto.request.SettlementRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static Settlement of(Book book, SettlementRequest request, String leaderUserEmail) {
        validateUsers(request.getUserEmails(), leaderUserEmail);

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

    private static void validateUsers(Set<String> userEmails, String leaderUserEmail) {
        userEmails.forEach(userId -> {
            if (userId.equals(leaderUserEmail)) {
                throw new IllegalArgumentException();
            }
        });
    }

    private static Integer calculateUserCount(Set<String> userEmails) {
        return userEmails.size() + 1;
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
}
