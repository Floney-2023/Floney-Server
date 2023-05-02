package com.floney.floney.book.entity;

import com.floney.floney.book.service.CategoryEnum;
import com.floney.floney.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@RequiredArgsConstructor
public class BookLine extends BaseEntity {

    @ManyToOne
    private BookUser writer;

    @ManyToOne
    private Book book;

    @OneToMany
    private Map<CategoryEnum, BookLineCategory> bookLineCategories;

    private Long money;

    private LocalDate lineDate;

    @Lob
    private String description;

    private Boolean exceptStatus;

    private Boolean status;

    @Builder
    public BookLine(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, BookUser writer, Book book, Long money, LocalDate lineDate, String description, Boolean exceptStatus, Boolean status) {
        super(id, createdAt, updatedAt);
        this.writer = writer;
        this.book = book;
        this.money = money;
        this.lineDate = lineDate;
        this.description = description;
        this.exceptStatus = exceptStatus;
        this.status = status;
    }

    public void add(CategoryEnum flow, BookLineCategory category) {
        this.bookLineCategories.put(flow, category);
    }

    public String getTargetCategory(CategoryEnum key) {
        return this.bookLineCategories.get(key).getName();
    }


}
