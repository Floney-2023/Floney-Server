package com.floney.floney.book.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SuperBuilder
@DiscriminatorValue("Default")
public class DefaultCategory extends Category {

    public static DefaultCategory rootParent() {
        return new DefaultCategory();
    }

    public DefaultCategory(String name, Category parent) {
        super(name, parent);
    }

}
