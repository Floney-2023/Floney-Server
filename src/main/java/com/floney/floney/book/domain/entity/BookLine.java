package com.floney.floney.book.domain.entity;

import com.floney.floney.book.domain.vo.AssetType;
import com.floney.floney.book.domain.vo.CategoryType;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.event.BookLineDeletedEvent;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.util.Events;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private BookUser writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bookLine", cascade = {CascadeType.ALL})
    @MapKeyEnumerated(EnumType.STRING)
    private final Map<CategoryType, BookLineCategory> bookLineCategories = new EnumMap<>(CategoryType.class);

    @Column(nullable = false)
    private Double money;

    @Column(nullable = false)
    private LocalDate lineDate;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean exceptStatus;

    @Builder
    private BookLine(BookUser writer, Book book, Double money, LocalDate lineDate, String description, Boolean exceptStatus) {
        this.writer = writer;
        this.book = book;
        this.money = money;
        this.lineDate = lineDate;
        this.description = description;
        this.exceptStatus = exceptStatus;
    }

    public void add(CategoryType type, BookLineCategory category) {
        this.bookLineCategories.put(type, category);
    }

    public String getTargetCategory(CategoryType key) {
        return this.bookLineCategories.get(key).getName();
    }

    public void update(BookLineRequest request) {
        this.money = request.getMoney();
        this.lineDate = request.getLineDate();
        this.description = request.getDescription();
        this.exceptStatus = request.getExcept();
    }

    public String getWriter() {
        return this.writer.getNickName();
    }

    public void inactive() {
        Events.raise(new BookLineDeletedEvent(this.getId()));
        this.status = Status.INACTIVE;
    }

    public boolean includedInAsset() {
        return !AssetType.BANK.getKind().equals(bookLineCategories.get(CategoryType.FLOW).getName());
    }
}
