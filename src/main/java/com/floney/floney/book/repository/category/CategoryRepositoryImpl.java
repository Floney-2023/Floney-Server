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

import javax.persistence.EntityManager;
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
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo> findSubcategoryInfos(final CategoryType parent, final String bookKey) {
        return jpaQueryFactory.select(
                new QCategoryInfo(constant(true), subcategory.name, subcategory.categoryKey)
            )
            .from(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                category.name.eq(parent),
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
    public Optional<Category> findByType(CategoryType categoryType) {
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
    public Optional<Subcategory> findSubcategory(final String name,
                                                 final Book targetBook,
                                                 final CategoryType parentName) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                subcategory.name.eq(name),
                category.name.eq(parentName),
                book.eq(targetBook)
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
    public List<BookLine> findAllBookLineBySubCategory(final Subcategory subCategory) {
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
    public Optional<Subcategory> findSubcategory(final Category parent,
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
    public List<Subcategory> findSubcategories(final Category parent,
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
    public void inactiveAllByBook(final Book book) {
        // unique index 가 걸려서 INACTIVE 데이터 먼저 삭제
        jpaQueryFactory.delete(subcategory)
            .where(
                subcategory.book.eq(book),
                subcategory.status.eq(INACTIVE)
            )
            .execute();

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

        entityManager.clear();
    }
}
