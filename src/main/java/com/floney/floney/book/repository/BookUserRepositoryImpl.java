package com.floney.floney.book.repository;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.user.entity.QUser.user;
import static com.querydsl.core.types.ExpressionUtils.count;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.process.QMyBookInfo;
import com.floney.floney.book.dto.process.QOurBookUser;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.MaxMemberException;
import com.floney.floney.common.exception.common.NoAuthorityException;
import com.floney.floney.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookUserRepositoryImpl implements BookUserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private static final int MAX_MEMBER = 4;
    private static final int OWNER = 1;

    @Override
    public void isMax(Book book) {
        int memberCount = jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .where(bookUser.book.eq(book),
                bookUser.status.eq(Status.ACTIVE))
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
                    user.profileImg,
                    user.email
                ))
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .where(book.bookKey.eq(bookKey), book.status.eq(Status.ACTIVE))
            .innerJoin(bookUser.user, user)
            .where(user.status.eq(Status.ACTIVE))
            .fetch();
    }

    @Override
    public Optional<BookUser> findBookUserByKey(String currentUserEmail, String bookKey) {
        return Optional.ofNullable(jpaQueryFactory
            .select(bookUser)
            .from(bookUser)
            .innerJoin(bookUser.user, user)
            .where(user.email.eq(currentUserEmail),
                user.status.eq(Status.ACTIVE))
            .innerJoin(bookUser.book, book)
            .where(book.bookKey.eq(bookKey),
                book.status.eq(Status.ACTIVE))
            .fetchOne());
    }


    @Override
    public List<MyBookInfo> findMyBooks(User user) {
        List<Book> books = jpaQueryFactory.select(book)
            .from(bookUser)
            .where(bookUser.user.eq(user),
                bookUser.status.eq(Status.ACTIVE))
            .fetch();

        List<MyBookInfo> infos = new ArrayList<>();
        for (Book target : books) {
            MyBookInfo my = jpaQueryFactory.select(
                    new QMyBookInfo(book.bookImg,
                        book.name,
                        bookUser.count(),
                        book.bookKey))
                .from(bookUser)
                .where(bookUser.book.eq(target),
                    bookUser.status.eq(Status.ACTIVE))
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
                bookUser.status.eq(Status.ACTIVE))
            .fetchOne();

        if (count > OWNER) {
            throw new NoAuthorityException();
        }
    }

    @Override
    public BookUser findBookUserBy(String email, Book target) {
        return jpaQueryFactory.selectFrom(bookUser)
            .innerJoin(bookUser.book, book)
            .where(book.eq(target),
                book.status.eq(Status.ACTIVE))
            .innerJoin(bookUser.user, user)
            .where(user.email.eq(email),
                user.status.eq(Status.ACTIVE))
            .fetchOne();

    }

    @Override
    public boolean existsByUserEmailAndBookKey(final String email, final String bookKey) {
        return jpaQueryFactory.selectFrom(bookUser)
                .innerJoin(bookUser.book, book)
                .innerJoin(bookUser.user, user)
                .where(book.bookKey.eq(bookKey), user.email.eq(email))
                .fetchOne() != null;
    }
}
