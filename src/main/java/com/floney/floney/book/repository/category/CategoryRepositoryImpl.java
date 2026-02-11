package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.process.QCategoryInfo;
import com.floney.floney.common.exception.book.InvalidCategoryRequestException;
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

    /**
     * Detects if the given text contains Korean (Hangul) characters.
     * Used to identify when clients send Korean names instead of categoryKey for default categories.
     *
     * @param text The text to check
     * @return true if the text contains Korean characters, false otherwise
     */
    private static boolean containsKorean(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            // Check for Hangul Syllables (AC00-D7AF) and Hangul Jamo (1100-11FF)
            if ((c >= '\uAC00' && c <= '\uD7AF') || (c >= '\u1100' && c <= '\u11FF')) {
                return true;
            }
        }
        return false;
    }

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
        // Strict validation: reject Korean names for default categories
        if (containsKorean(name)) {
            // Try to find by name (for user-defined categories)
            Subcategory result = jpaQueryFactory.selectFrom(subcategory)
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
                .fetchOne();

            // If found and has categoryKey, it's a default category - reject Korean name
            if (result != null && result.getCategoryKey() != null) {
                throw new InvalidCategoryRequestException(name, result.getCategoryKey());
            }

            // If found with no categoryKey, it's a valid user-defined category
            return Optional.ofNullable(result);
        }

        // For English/alphanumeric input, try categoryKey first (for default categories)
        Subcategory result = jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                subcategory.categoryKey.eq(name),
                category.name.eq(parentName),
                book.eq(targetBook)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                category.status.eq(ACTIVE),
                book.status.eq(ACTIVE)
            )
            .fetchOne();

        // If not found by categoryKey, try to find by name (for user-defined categories with English names)
        if (result == null) {
            result = jpaQueryFactory.selectFrom(subcategory)
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
                .fetchOne();
        }

        return Optional.ofNullable(result);
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
        // Strict validation: reject Korean names for default categories
        if (containsKorean(name)) {
            // Try to find by name (for user-defined categories)
            Subcategory result = jpaQueryFactory.selectFrom(subcategory)
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
                .fetchOne();

            // If found and has categoryKey, it's a default category - reject Korean name
            if (result != null && result.getCategoryKey() != null) {
                throw new InvalidCategoryRequestException(name, result.getCategoryKey());
            }

            // If found with no categoryKey, it's a valid user-defined category
            return Optional.ofNullable(result);
        }

        // For English/alphanumeric input, try categoryKey first (for default categories)
        Subcategory result = jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                category.eq(parent),
                book.eq(targetBook),
                subcategory.categoryKey.eq(name)
            )
            .where(
                subcategory.status.eq(ACTIVE),
                book.status.eq(ACTIVE),
                category.status.eq(ACTIVE)
            )
            .fetchOne();

        // If not found by categoryKey, try to find by name (for user-defined categories with English names)
        if (result == null) {
            result = jpaQueryFactory.selectFrom(subcategory)
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
                .fetchOne();
        }

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subcategory> findSubcategoryByCategoryKey(final String categoryKey,
                                                              final Book targetBook,
                                                              final CategoryType parentName) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(subcategory)
            .innerJoin(subcategory.parent, category)
            .innerJoin(subcategory.book, book)
            .where(
                subcategory.categoryKey.eq(categoryKey),
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
