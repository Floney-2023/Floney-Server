package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.process.QCategoryInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.category.entity.QCategory.category;
import static com.floney.floney.book.domain.category.entity.QSubcategory.subcategory;
import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QBookLine.bookLine;
import static com.floney.floney.book.domain.entity.QBookLineCategory.bookLineCategory;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static com.querydsl.core.types.dsl.Expressions.constant;

@Repository
@Transactional
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryCustomRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo> findAllSubCategoryByCategoryType(final CategoryType categoryType, final String bookKey) {
        return jpaQueryFactory.select(
                new QCategoryInfo(constant(true), subcategory.name)
            )
            .from(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                category.name.eq(categoryType),
                book.bookKey.eq(bookKey)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findLineCategory(CategoryType categoryType) {
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
    public Optional<Subcategory> findAssetSubCategory(final String name, final Book targetBook) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.book, book)
            .innerJoin(subcategory.parent, category)
            .where(
                book.eq(targetBook),
                category.name.eq(CategoryType.ASSET),
                subcategory.name.eq(name)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subcategory> findLineSubCategory(final String name,
                                                     final Book targetBook,
                                                     final Category parent) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                subcategory.name.eq(name),
                category.eq(parent),
                book.eq(book)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                category.status.eq(ACTIVE),
                book.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLine> findAllBookLineByCategory(final Subcategory subCategory) {
        return jpaQueryFactory.selectFrom(bookLine)
            .innerJoin(bookLine.categories, bookLineCategory)
            .where(
                bookLineCategory.assetSubcategory.eq(subCategory)
                    .or(bookLineCategory.lineSubcategory.eq(subCategory))
            )
            .where(
                bookLine.status.eq(ACTIVE),
                bookLineCategory.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subcategory> findCustomTarget(final Category parent,
                                                  final Book targetBook,
                                                  final String name) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                category.eq(parent),
                book.eq(targetBook),
                subcategory.name.eq(name)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetchOne());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategory> findAllLineSubCategoryByLineCategory(final Category parent,
                                                                  final String bookKey) {
        return jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                category.eq(parent),
                book.bookKey.eq(bookKey)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetch();
    }

    @Override
    @Transactional(readOnly = true)
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
        final long result = jpaQueryFactory.update(subcategory)
            .set(subcategory.status, INACTIVE)
            .set(subcategory.updatedAt, LocalDateTime.now())
            .where(
                subcategory.book.eq(book)
            )
            .where(
                subcategory.status.eq(ACTIVE)
            )
            .execute();

        if (result == 0) {
            logger.warn("inactiveAllByBook 쿼리에서 변경된 row가 없음 - 가계부: {}", book.getId());
        }
    }
}
