package com.floney.floney.common;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@AllArgsConstructor
@Getter
@MappedSuperclass
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Boolean status;

    protected BaseEntity() {
    }

    protected BaseEntity(Long id) {
        this.id = id;
    }
}
