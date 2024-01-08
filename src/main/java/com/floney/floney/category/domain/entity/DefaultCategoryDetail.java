package com.floney.floney.category.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@SuperBuilder
@DiscriminatorValue("DEFAULT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultCategoryDetail extends CategoryDetail {

}
