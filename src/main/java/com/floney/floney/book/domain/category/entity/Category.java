package com.floney.floney.book.domain.category.entity;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.book.domain.category.CategoryType.OUTCOME;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends BaseEntity {

    public static final int FAVORITE_MAX_SIZE = 15;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CategoryType name;

    public boolean isIncomeOrOutcome() {
        return INCOME.equals(name) || OUTCOME.equals(name);
    }

    public boolean isIncome() {
        return INCOME.equals(name);
    }

    public boolean isOutcome() {
        return OUTCOME.equals(name);
    }

    public void validateLine() {
        name.validateLineType();
    }
}
