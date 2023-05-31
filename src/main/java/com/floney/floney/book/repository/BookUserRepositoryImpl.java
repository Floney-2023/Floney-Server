package com.floney.floney.book.repository;

import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.dto.OurBookUser;
import com.floney.floney.book.dto.QMyBookInfo;
import com.floney.floney.book.dto.QOurBookUser;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.common.exception.MaxMemberException;
import com.floney.floney.common.exception.NoAuthorityException;
import com.floney.floney.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.user.entity.QUser.user;
import static com.querydsl.core.types.ExpressionUtils.count;

@Repository
@RequiredArgsConstructor
public class BookUserRepositoryImpl implements BookUserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private static final int MAX_MEMBER = 4;
    private static final int OWNER = 1;
    private static final boolean ACTIVE = true;

    @Override
    public void isMax(Book book) {
        int memberCount = jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .where(bookUser.book.eq(book),
                bookUser.status.eq(ACTIVE))
            .fetch()
            .size();

        if (memberCount > MAX_MEMBER) {
            throw new MaxMemberException();
        }
    }

    @Override
    public List<OurBookUser> findAllUser(String bookKey) {
        return jpaQueryFactory.select(
                new QOurBookUser(
                    user.nickname,
                    user.profileImg
                ))
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .where(book.bookKey.eq(bookKey), book.status.eq(ACTIVE))
            .innerJoin(bookUser.user, user)
            .where(user.status.eq(ACTIVE))
            .fetch();
    }

    @Override
    public Optional<BookUser> findUserWith(String nickName, String bookKey) {
        return Optional.ofNullable(jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .innerJoin(bookUser.user, user)
            .where(user.nickname.eq(nickName),
                user.status.eq(ACTIVE))
            .innerJoin(bookUser.book, book)
            .where(book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE))
            .fetchOne());
    }

    @Override
    public List<MyBookInfo> findMyBooks(User user) {
        List<Book> books = jpaQueryFactory.select(book)
            .from(bookUser)
            .where(bookUser.user.eq(user),
                bookUser.status.eq(ACTIVE))
            .fetch();

        List<MyBookInfo> infos = new ArrayList<>();
        for (Book target : books) {
            MyBookInfo my = jpaQueryFactory.select(
                    new QMyBookInfo(book.profileImg,
                        book.name,
                        bookUser.count(),
                        book.bookKey))
                .from(bookUser)
                .where(bookUser.book.eq(target),
                    bookUser.status.eq(ACTIVE))
                .fetchOne();
            infos.add(my);
        }
        return infos;
    }

    @Override
    public void countBookUser(Book target) {
        Long count = jpaQueryFactory.select(count(bookUser))
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .where(book.eq(target),
                bookUser.status.eq(ACTIVE))
            .fetchOne();

        if (count > OWNER) {
            throw new NoAuthorityException();
        }
    }

    @Override
    public BookUser findByEmailAndBook(String email, Book target) {
        return jpaQueryFactory.selectFrom(bookUser)
            .innerJoin(bookUser.book, book)
            .where(book.eq(target),
                book.status.eq(ACTIVE))
            .innerJoin(bookUser.user, user)
            .where(user.email.eq(email),
                user.status.eq(ACTIVE))
            .fetchOne();

    }


}
