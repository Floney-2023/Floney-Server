package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.MyBookInfo;
import com.floney.floney.book.dto.process.OurBookUser;
import com.floney.floney.book.dto.process.QMyBookInfo;
import com.floney.floney.book.dto.process.QOurBookUser;
import com.floney.floney.user.entity.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookUser.bookUser;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.floney.floney.user.entity.QUser.user;

@Repository
@Transactional
@RequiredArgsConstructor
public class BookUserRepositoryImpl implements BookUserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<OurBookUser> findAllUser(final String bookKey) {
        return jpaQueryFactory.select(
                new QOurBookUser(
                    user.nickname,
                    user.profileImg,
                    user.email
                ))
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .innerJoin(bookUser.user, user)
            .where(
                book.bookKey.eq(bookKey)
            )
            .where(
                user.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findOldestBookUserEmailExceptOwner(final User owner,
                                                               final Book targetBook) {
        return Optional.ofNullable(jpaQueryFactory.select(user.email)
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .innerJoin(bookUser.user, user)
            .where(
                book.eq(targetBook),
                user.ne(owner)
            )
            .where(
                book.status.eq(ACTIVE),
                user.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE)
            )
            .orderBy(bookUser.createdAt.asc())
            .fetchFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookUser> findBookUserByEmailAndBookKey(final String userEmail,
                                                            final String bookKey) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(bookUser)
            .innerJoin(bookUser.book, book).fetchJoin()
            .innerJoin(bookUser.user, user).fetchJoin()
            .where(
                user.email.eq(userEmail),
                book.bookKey.eq(bookKey)
            )
            .where(
                user.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE),
                book.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBookKeyAndUserEmail(final String bookKey,
                                               final String userEmail) {
        return jpaQueryFactory.select(bookUser.id)
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
            .fetchOne() != null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookUser> findBookUserByCode(final String userEmail,
                                                 final String bookCode) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(bookUser)
            .innerJoin(bookUser.user, user).fetchJoin()
            .innerJoin(bookUser.book, book).fetchJoin()
            .where(
                book.code.eq(bookCode),
                user.email.eq(userEmail)
            )
            .where(
                book.status.eq(ACTIVE),
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyBookInfo> findMyBookInfos(final User targetUser) {
        final JPQLQuery<Long> booksByUser = JPAExpressions.select(book.id)
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .where(
                bookUser.user.eq(targetUser)
            )
            .where(
                bookUser.status.eq(ACTIVE),
                book.status.eq(ACTIVE)
            );

        return jpaQueryFactory.select(
                new QMyBookInfo(
                    book.bookImg,
                    book.bookKey,
                    book.name,
                    bookUser.count()
                ))
            .from(bookUser)
            .where(
                bookUser.status.eq(ACTIVE),
                bookUser.book.id.in(booksByUser)
            )
            .groupBy(bookUser.book)
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public int countByBook(final Book targetBook) {
        return jpaQueryFactory.select(bookUser.id)
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .where(
                book.eq(targetBook)
            )
            .where(
                bookUser.status.eq(ACTIVE)
            )
            .fetch()
            .size();
    }

    @Override
    public int countByBookExclusively(final Book target) {
        return jpaQueryFactory.select(bookUser.id)
            .from(bookUser)
            .innerJoin(bookUser.book, book)
            .where(
                book.eq(target)
            )
            .where(
                bookUser.status.eq(ACTIVE)
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch()
            .size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookUser> findAllByUserId(final Long userId) {
        return jpaQueryFactory.selectFrom(bookUser)
            .innerJoin(bookUser.user, user).fetchJoin()
            .where(
                user.id.eq(userId)
            )
            .where(
                bookUser.status.eq(ACTIVE),
                user.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(bookUser)
            .set(bookUser.status, INACTIVE)
            .set(bookUser.updatedAt, LocalDateTime.now())
            .where(
                bookUser.book.eq(book),
                bookUser.status.eq(ACTIVE)
            )
            .execute();

        entityManager.clear();
    }
}
