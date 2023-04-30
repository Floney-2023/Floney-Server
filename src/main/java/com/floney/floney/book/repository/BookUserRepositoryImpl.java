package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.common.exception.MaxMemberException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.floney.floney.book.entity.QBookUser.bookUser;

@Repository
@RequiredArgsConstructor
public class BookUserRepositoryImpl implements BookUserCustomRepository {
    private static final int MAX_MEMBER = 4;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void isMax(Book book) {
        int memberCount = jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .where(bookUser.book.eq(book))
            .fetch()
            .size();

        if (memberCount > MAX_MEMBER) {
            throw new MaxMemberException();
        }
    }
}
