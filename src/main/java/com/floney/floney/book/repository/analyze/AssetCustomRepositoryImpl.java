package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.floney.floney.book.domain.entity.QAsset.asset;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetCustomRepositoryImpl implements AssetCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllBy(final Book book) {
        // unique index 가 걸려서 INACTIVE 데이터 먼저 삭제
        jpaQueryFactory.delete(asset)
            .where(
                asset.book.eq(book),
                asset.status.eq(INACTIVE)
            )
            .execute();
        
        jpaQueryFactory.update(asset)
            .set(asset.status, INACTIVE)
            .set(asset.updatedAt, LocalDateTime.now())
            .where(
                asset.book.eq(book),
                asset.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }

    @Override
    @Transactional
    public void subtractMoneyByDateAndBook(final double money, final LocalDate date, final Book book) {
        jpaQueryFactory.update(asset)
            .set(asset.money, asset.money.subtract(money))
            .set(asset.updatedAt, LocalDateTime.now())
            .where(
                asset.book.eq(book),
                asset.date.eq(date),
                asset.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }
}
