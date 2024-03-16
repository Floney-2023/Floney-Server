package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.entity.Subcategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.floney.floney.book.domain.category.entity.QSubcategory.subcategory;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SubcategoryRepositoryImpl implements SubcategoryCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public void inactive(final Subcategory targetSubcategory) {
        // unique index 가 걸려서 INACTIVE 데이터 먼저 삭제
        queryFactory.delete(subcategory)
            .where(
                subcategory.book.eq(targetSubcategory.getBook()),
                subcategory.parent.eq(targetSubcategory.getParent()),
                subcategory.name.eq(targetSubcategory.getName()),
                subcategory.status.eq(INACTIVE)
            )
            .execute();

        final long result = queryFactory.update(subcategory)
            .set(subcategory.status, INACTIVE)
            .set(subcategory.updatedAt, LocalDateTime.now())
            .where(
                subcategory.eq(targetSubcategory)
            )
            .where(
                subcategory.status.eq(ACTIVE)
            )
            .execute();

        if (result == 0) {
            log.warn("Subcategory가 삭제되지 않음 - id: {}", targetSubcategory.getId());
        }

        entityManager.clear();
    }
}
