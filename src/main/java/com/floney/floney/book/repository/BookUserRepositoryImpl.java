package com.floney.floney.book.repository;

import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.process.QMyBookInfo;
import com.floney.floney.book.dto.process.QOurBookUser;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.user.entity.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookUser.bookUser;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookUserRepositoryImpl implements BookUserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

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
                .where(
                        book.bookKey.eq(bookKey),
                        book.status.eq(ACTIVE)
                    ,bookUser.status.eq(ACTIVE)
                )
                .innerJoin(bookUser.user, user)
                .where(user.status.eq(ACTIVE))
                .fetch();
    }

    @Override
    public Optional<BookUser> findBookUserByEmailAndBookKey(String userEmail, String bookKey) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(bookUser)
                .innerJoin(bookUser.book, book)
                .where(
                        book.bookKey.eq(bookKey),
                        book.status.eq(ACTIVE)
                )
                .innerJoin(bookUser.user, user)
                .where(
                        user.email.eq(userEmail),
                        user.status.eq(ACTIVE)
                )
                .fetchOne());
    }

    @Override
    public boolean existsByBookKeyAndUserEmail(final String bookKey, final String userEmail) {
        return jpaQueryFactory.select(bookUser.id)
                .from(bookUser)
                .innerJoin(bookUser.book, book)
                .where(
                        book.bookKey.eq(bookKey),
                        book.status.eq(ACTIVE)
                )
                .innerJoin(bookUser.user, user)
                .where(
                        user.email.eq(userEmail),
                        user.status.eq(ACTIVE)
                )
                .fetchOne() != null;
    }

    @Override
    public Optional<BookUser> findBookUserByKey(String currentUserEmail, String bookKey) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(bookUser)
                .innerJoin(bookUser.user, user)
                .where(
                        user.email.eq(currentUserEmail),
                        user.status.eq(ACTIVE)
                )
                .innerJoin(bookUser.book, book)
                .where(
                        book.bookKey.eq(bookKey),
                        book.status.eq(ACTIVE)
                )
                .fetchOne());
    }

    @Override
    public Optional<BookUser> findBookUserByCode(String currentUserEmail, String bookCode) {
        return Optional.ofNullable(jpaQueryFactory
                .select(bookUser)
                .from(bookUser)
                .innerJoin(bookUser.user, user)
                .where(user.email.eq(currentUserEmail),
                        user.status.eq(ACTIVE))
                .innerJoin(bookUser.book, book)
                .where(book.code.eq(bookCode),
                        book.status.eq(ACTIVE))
                .fetchOne());
    }


    @Override
    public List<MyBookInfo> findMyBookInfos(User user) {
        List<Book> books = jpaQueryFactory.select(book)
                .from(bookUser)
                .where(bookUser.user.eq(user),
                        bookUser.status.eq(ACTIVE))
                .fetch();

        List<MyBookInfo> infos = new ArrayList<>();
        for (Book target : books) {
            MyBookInfo my = jpaQueryFactory.select(
                            new QMyBookInfo(book.bookImg,
                                    book.name,
                                    bookUser.count(),
                                    book.bookKey,
                                    book.bookStatus))
                    .from(bookUser)
                    .where(bookUser.book.eq(target),
                            bookUser.status.eq(ACTIVE))
                    .fetchOne();
            infos.add(my);
        }
        return infos;
    }


    @Override
    public List<Book> findMyInactiveBooks(User user) {
        List<Book> books = jpaQueryFactory.select(book)
                .from(bookUser)
                .where(bookUser.user.eq(user),
                        bookUser.status.eq(ACTIVE))
                .fetch();

        List<Book> myBooks = new ArrayList<>();

        for (Book target : books) {
            if (target.getBookStatus() == INACTIVE && target.getStatus() == ACTIVE) {
                myBooks.add(target);
            }
        }
        return myBooks;

    }

    @Override
    public List<Book> findBookByOwner(User user) {
        List<Book> books = jpaQueryFactory.select(book)
                .from(bookUser)
                .where(bookUser.user.eq(user),
                        bookUser.status.eq(ACTIVE))
                .fetch();

        List<Book> myBooks = new ArrayList<>();
        for (Book target : books) {
            if (Objects.equals(target.getOwner(), user.getEmail())) {
                myBooks.add(target);
            }
        }
        return myBooks;

    }

    @Override
    public int countByBook(final Book target) {
        return jpaQueryFactory
                .select(bookUser.id)
                .from(bookUser)
                .innerJoin(bookUser.book, book)
                .where(
                        book.eq(target),
                        bookUser.status.eq(ACTIVE)
                )
                .fetch()
                .size();
    }

    @Override
    public int countByBookExclusively(final Book target) {
        return jpaQueryFactory
                .select(bookUser.id)
                .from(bookUser)
                .innerJoin(bookUser.book, book)
                .where(
                        book.eq(target),
                        bookUser.status.eq(ACTIVE)
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch()
                .size();
    }

    @Override
    public BookUser findBookUserBy(String email, Book target) {
        return jpaQueryFactory.selectFrom(bookUser)
                .innerJoin(bookUser.book, book)
                .where(book.eq(target),
                        book.status.eq(ACTIVE))
                .innerJoin(bookUser.user, user)
                .where(user.email.eq(email),
                        user.status.eq(ACTIVE))
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

    @Override
    @Transactional
    public Optional<User> findRandomBookUserWhoSubscribeExclusively(Book targetBook) {
        // TODO: 반환 타입 User에서 BookUser로 변경
        return Optional.ofNullable(jpaQueryFactory.selectFrom(user)
                .where(
                        user.subscribe.eq(true),
                        user.in(
                                JPAExpressions.select(bookUser.user)
                                        .from(bookUser)
                                        .where(
                                                bookUser.book.eq(targetBook)
                                        )
                        )
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchFirst());
    }

    @Override
    public List<BookUser> findAllByUserId(final Long userId) {
        return jpaQueryFactory.selectFrom(bookUser)
                .innerJoin(book)
                .on(book.eq(bookUser.book)).fetchJoin()
                .innerJoin(user)
                .on(user.id.eq(userId))
                .where(bookUser.status.eq(ACTIVE))
                .fetch();
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(bookUser)
                .set(bookUser.status, INACTIVE)
                .set(bookUser.updatedAt, LocalDateTime.now())
                .where(
                        bookUser.book.eq(book),
                        bookUser.status.eq(ACTIVE)
                )
                .execute();
    }
}
