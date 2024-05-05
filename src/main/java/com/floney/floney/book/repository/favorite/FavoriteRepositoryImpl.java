package com.floney.floney.book.repository.favorite;


import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static com.floney.floney.book.domain.favorite.QFavorite.favorite;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(favorite)
                .set(favorite.status, INACTIVE)
                .set(favorite.updatedAt, LocalDateTime.now())
                .where(
                        favorite.book.eq(book),
                        favorite.status.eq(ACTIVE)
                )
                .execute();

        entityManager.clear();
    }

    @Override
    public void inactiveAllBySubcategory(final Subcategory subcategory) {
        jpaQueryFactory.update(favorite)
                .set(favorite.status, INACTIVE)
                .set(favorite.updatedAt, LocalDateTime.now())
                .where(
                        favorite.lineSubcategory.eq(subcategory)
                                .or(favorite.assetSubcategory.eq(subcategory))
                )
                .where(
                        favorite.status.eq(ACTIVE)
                )
                .execute();

        entityManager.clear();
    }
}

