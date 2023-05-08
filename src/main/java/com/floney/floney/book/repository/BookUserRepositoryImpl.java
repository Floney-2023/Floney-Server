package com.floney.floney.book.repository;

import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.dto.QMyBookInfo;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.common.exception.MaxMemberException;
import com.floney.floney.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.user.entity.QUser.user;

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

    @Override
    public BookUser findUserWith(String nickName, String bookKey) {
        return jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .innerJoin(bookUser.user, user)
            .where(user.nickname.eq(nickName))
            .innerJoin(bookUser.book, book)
            .where(book.bookKey.eq(bookKey))
            .fetchOne();
    }

    @Override
    public BooleanExpression existBookUser(String email, String code) {
        return jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .innerJoin(bookUser.user, user)
            .where(user.email.eq(email))
            .innerJoin(bookUser.book, book)
            .where(book.code.eq(code))
            .exists();
    }

    @Override
    public List<MyBookInfo> findMyBooks(User user) {
        List<Book> books = jpaQueryFactory.select(book)
            .from(bookUser)
            .where(bookUser.user.eq(user))
            .fetch();

        List<MyBookInfo> infos = new ArrayList<>();
        for (Book target : books) {
            MyBookInfo my = jpaQueryFactory.select(new QMyBookInfo(book.profileImg, book.name, bookUser.count()))
                .from(bookUser)
                .where(bookUser.book.eq(target))
                .fetchOne();
            infos.add(my);
        }
        return infos;
    }


}
