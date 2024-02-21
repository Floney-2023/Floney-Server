package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.user.entity.QUser.user;

@Repository
@Transactional
@RequiredArgsConstructor
public class BookCustomRepositoryImpl implements BookCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByBookUserEmailAndBookKey(final String userEmail,
                                                        final String bookKey) {
        return Optional.ofNullable(jpaQueryFactory.select(book)
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .innerJoin(bookUser.user, user)
            .where(
                user.email.eq(userEmail),
                book.bookKey.eq(bookKey)
            )
            .where(
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                book.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAllByUserEmail(final String userEmail) {
        return jpaQueryFactory.select(book)
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .innerJoin(bookUser.user, user)
            .where(
                user.email.eq(userEmail)
            )
            .where(
                book.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE)
            )
            .fetch();
    }
}
