package com.floney.floney.book.repository;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.user.entity.QUser.user;

import com.floney.floney.book.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Book> findByBookUserEmailAndBookKey(final String userEmail, final String bookKey) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(bookUser)
                .innerJoin(bookUser.book, book).fetchJoin()
                .innerJoin(bookUser.user, user).fetchJoin()
                .where(
                        user.email.eq(userEmail), book.bookKey.eq(bookKey),
                        book.status.eq(ACTIVE), user.status.eq(ACTIVE)
                )
                .fetchOne().getBook());
    }
}
