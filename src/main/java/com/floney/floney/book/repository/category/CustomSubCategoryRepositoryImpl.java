package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.Category;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.CustomSubCategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.process.QCategoryInfo;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.category.QCategory.category;
import static com.floney.floney.book.domain.category.QCustomSubCategory.customSubCategory;
import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookLine.bookLine;
import static com.floney.floney.book.domain.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.querydsl.core.types.dsl.Expressions.constant;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomSubCategoryRepositoryImpl implements CategoryCustomRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo> findAllCategory(final String parentName, final String bookKey) {
        final CategoryType categoryType = CategoryType.findByMeaning(parentName);
        return jpaQueryFactory.select(
                new QCategoryInfo(constant(true), customSubCategory.name)
            )
            .from(customSubCategory)
            .innerJoin(customSubCategory.parent, category)
            .innerJoin(customSubCategory.book, book)
            .where(
                category.name.eq(categoryType),
                book.bookKey.eq(bookKey)
            )
            .where(
                customSubCategory.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findLineCategory(final String name) {
        final CategoryType categoryType = CategoryType.findLineByMeaning(name);
        return Optional.ofNullable(jpaQueryFactory.selectFrom(category)
            .where(
                category.name.eq(categoryType)
            )
            .where(
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomSubCategory> findAssetSubCategory(final String name, final String bookKey) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(customSubCategory)
            .innerJoin(customSubCategory.book, book)
            .innerJoin(customSubCategory.parent, category)
            .where(
                book.bookKey.eq(bookKey),
                category.name.eq(CategoryType.ASSET),
                customSubCategory.name.eq(name)
            )
            .where(
                customSubCategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomSubCategory> findLineSubCategory(final String name,
                                                           final String bookKey,
                                                           final String parentName) {
        final CategoryType categoryType = CategoryType.findLineByMeaning(parentName);
        return Optional.ofNullable(jpaQueryFactory.selectFrom(customSubCategory)
            .innerJoin(customSubCategory.parent, category)
            .innerJoin(customSubCategory.book, book)
            .where(
                category.name.eq(categoryType),
                book.bookKey.eq(bookKey),
                customSubCategory.name.eq(name)
            )
            .where(
                customSubCategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findAllBookLineByCategory(final CustomSubCategory subCategory) {
        return jpaQueryFactory.selectFrom(bookLine)
            .innerJoin(bookLine.categories, bookLineCategory)
            .where(
                bookLineCategory.assetSubCategory.eq(subCategory)
                    .or(bookLineCategory.lineSubCategory.eq(subCategory))
            )
            .where(
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    public Optional<CustomSubCategory> findCustomTarget(final Category parent,
                                                        final Book targetBook,
                                                        final String name) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(customSubCategory)
            .innerJoin(customSubCategory.parent, category)
            .innerJoin(customSubCategory.book, book)
            .where(
                category.eq(parent),
                book.eq(targetBook),
                customSubCategory.name.eq(name)
            )
            .where(
                customSubCategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    public void inactiveCustomCategory(final String parentName,
                                       final String name,
                                       final String bookKey) {
        final JPQLQuery<Book> bookByBookKey = JPAExpressions.selectFrom(book)
            .where(
                book.bookKey.eq(bookKey),
                book.status.eq(ACTIVE)
            );

        final CategoryType categoryType = CategoryType.findByMeaning(parentName);
        final JPQLQuery<Category> parentByName = JPAExpressions.selectFrom(category)
            .where(
                category.name.eq(categoryType),
                category.status.eq(ACTIVE)
            );

        final long result = jpaQueryFactory.update(customSubCategory)
            .set(customSubCategory.status, INACTIVE)
            .set(customSubCategory.updatedAt, LocalDateTime.now())
            .where(
                customSubCategory.name.eq(name),
                customSubCategory.status.eq(ACTIVE),
                customSubCategory.book.eq(bookByBookKey),
                customSubCategory.parent.eq(parentByName)
            )
            .execute();

        if (result == 0) {
            logger.warn(
                "inactiveCustomCategory 쿼리에서 변경된 row가 없음 - 상위 카테고리: {}, 하위 카테고리: {}, 가계부 키: {}",
                parentName, name, bookKey
            );
        }
    }

    @Override
    public List<CustomSubCategory> findAllCustomCategory(final Book book) {
        return jpaQueryFactory.selectFrom(customSubCategory)
            .where(
                customSubCategory.book.eq(book)
            )
            .where(
                customSubCategory.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    public List<CustomSubCategory> findAllCustomChildCategoryByRoot(final Category parent,
                                                                    final String bookKey) {
        return jpaQueryFactory.selectFrom(customSubCategory)
            .innerJoin(customSubCategory.parent, category)
            .innerJoin(customSubCategory.book, book)
            .where(
                category.eq(parent),
                book.bookKey.eq(bookKey)
            )
            .where(
                customSubCategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    public Optional<Category> findParentCategory(final String name) {
        final CategoryType categoryType = CategoryType.findByMeaning(name);
        return Optional.ofNullable(jpaQueryFactory.selectFrom(category)
            .where(
                category.name.eq(categoryType)
            )
            .where(
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    public void inactiveAllByBook(final Book book) {
        final long result = jpaQueryFactory.update(customSubCategory)
            .set(customSubCategory.status, INACTIVE)
            .set(customSubCategory.updatedAt, LocalDateTime.now())
            .where(
                customSubCategory.book.eq(book)
            )
            .where(
                customSubCategory.status.eq(ACTIVE)
            )
            .execute();

        if (result == 0) {
            logger.warn("inactiveAllByBook 쿼리에서 변경된 row가 없음 - 가계부: {}", book.getId());
        }
    }
}
