package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.floney.floney.book.entity.QCategory.category;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Category findRoot(String root) {
        return jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(root), category.parent.isNull())
            .fetchOne();
    }

}
