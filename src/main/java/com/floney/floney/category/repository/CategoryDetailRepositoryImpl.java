package com.floney.floney.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CategoryDetailRepositoryImpl implements CategoryDetailRepository {

    private final JPAQueryFactory jpaQueryFactory;
}
