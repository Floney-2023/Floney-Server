package com.floney.floney.book.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SuperBuilder
@DiscriminatorValue("DefaultRoot")
public class RootCategory extends Category {
}
