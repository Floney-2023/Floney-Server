package com.floney.floney.common;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


@AllArgsConstructor
@Getter
@MappedSuperclass
@DynamicInsert
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

    protected BaseEntity() {
    }

    protected BaseEntity(Long id) {
        this.id = id;
    }
}
