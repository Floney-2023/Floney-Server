package com.floney.floney.book.entity;

import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
public class BookLine extends BaseEntity {

    @ManyToOne
    private BookUser writer;

    @ManyToOne
    private Book book;

    @OneToMany
    private Map<CategoryEnum, BookLineCategory> bookLineCategories = new EnumMap<>(CategoryEnum.class);

    private Long money;

    private LocalDate lineDate;

    @Lob
    private String description;

    private Boolean exceptStatus;


    @Builder
    public BookLine( BookUser writer, Book book, Long money, LocalDate lineDate, String description, Boolean exceptStatus) {
        this.writer = writer;
        this.book = book;
        this.money = money;
        this.lineDate = lineDate;
        this.description = description;
        this.exceptStatus = exceptStatus;
    }

    public void add(CategoryEnum flow, BookLineCategory category) {
        this.bookLineCategories.put(flow, category);
    }

    public String getTargetCategory(CategoryEnum key) {
        return this.bookLineCategories.get(key).getName();
    }


}
