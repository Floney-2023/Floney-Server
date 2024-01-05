package com.floney.floney.book.domain.entity.category;

import com.floney.floney.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@Inheritance
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Category extends BaseEntity {

    @Column(length = 10)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category parent;

    protected Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

}
