package com.floney.floney.common;

import com.floney.floney.common.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
@MappedSuperclass
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @CreatedDate
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @Column
    @Enumerated(EnumType.STRING)
    protected Status status;

    protected BaseEntity() {
        this.status = Status.ACTIVE;
    }

    protected BaseEntity(Long id) {
        this.id = id;
    }

    public void delete() {
        this.status = Status.INACTIVE;
    }

    public boolean isInactive() {
        return this.status == Status.INACTIVE;
    }
}
