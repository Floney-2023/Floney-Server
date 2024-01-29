package com.floney.floney.book.domain.entity;

import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.event.BookLineDeletedEvent;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.entity.BaseEntity;
import com.floney.floney.common.util.Events;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private BookUser writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @OneToOne(mappedBy = "bookLine", cascade = CascadeType.ALL)
    private BookLineCategory categories;

    @Column(nullable = false)
    private Double money;

    @Column(nullable = false)
    private LocalDate lineDate;

    @Column
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean exceptStatus;

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
        return categories.isIncomeOrOutcome();
    }
}
