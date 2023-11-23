package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.domain.entity.DefaultCategory;
import com.floney.floney.book.domain.entity.RootCategory;
import com.floney.floney.book.domain.entity.category.BookCategory;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.process.QCategoryInfo;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.entity.QBook.book;
import static com.floney.floney.book.domain.entity.QCategory.category;
import static com.floney.floney.book.domain.entity.category.QBookCategory.bookCategory;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    private static final boolean DEFAULT = true;
    private static final boolean CUSTOM = false;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CategoryInfo> findAllCategory(String name, String bookKey) {
        Category targetRoot = jpaQueryFactory.selectFrom(category)
                .where(
                        category.name.eq(name),
                        category.instanceOf(RootCategory.class)
                )
                .fetchOne();

        List<CategoryInfo> children = jpaQueryFactory.select(
                        new QCategoryInfo(Expressions.constant(DEFAULT), category.name)
                )
                .from(category)
                .where(
                        category.parent.eq(targetRoot),
                        category.instanceOf(DefaultCategory.class)
                )
                .fetch();

        children.addAll(
                jpaQueryFactory.select(new QCategoryInfo(Expressions.constant(CUSTOM), bookCategory.name))
                        .from(bookCategory)
                        .innerJoin(bookCategory.parent, category)
                        .where(category.eq(targetRoot))
                        .innerJoin(bookCategory.book, book)
                        .where(
                                book.bookKey.eq(bookKey),
                                bookCategory.status.eq(ACTIVE)
                        )
                        .fetch()
        );

        return children;
    }

    @Override
    public Optional<Category> findFlowCategory(String name) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(category)
                .where(
                        category.name.eq(name),
                        category.instanceOf(RootCategory.class),
                        category.parent.isNull()
                )
                .fetchOne());
    }

    @Override
    public Optional<Category> findAssetCategory(String name, String bookKey) {
        Optional<Category> target = Optional.ofNullable(jpaQueryFactory.selectFrom(category)
                .where(category.name.eq(name))
                .innerJoin(category)
                .where(
                        category.parent.name.eq("자산"),
                        category.instanceOf(DefaultCategory.class)
                )
                .fetchOne());

        if (target.isEmpty()) {
            target = Optional.ofNullable(jpaQueryFactory
                    .selectFrom(bookCategory)
                    .innerJoin(bookCategory.book, book)
                    .where(
                            book.bookKey.eq(bookKey),
                            bookCategory.name.eq(name),
                            bookCategory.parent.name.eq("자산"),
                            bookCategory.status.eq(ACTIVE)
                    )
                    .fetchOne());
        }
        return target;

    }


    @Override
    public Optional<Category> findLineCategory(String name, String bookKey, String parent) {
        Category parentFlowCategory = findFlowCategory(parent)
                .orElseThrow(() -> new NotFoundCategoryException(parent));

        Optional<Category> target = Optional.ofNullable(jpaQueryFactory.selectFrom(category)
                .where(
                        category.name.eq(name),
                        category.parent.eq(parentFlowCategory),
                        category.instanceOf(DefaultCategory.class)
                )
                .fetchOne());

        if (target.isEmpty()) {
            target = Optional.ofNullable(jpaQueryFactory
                    .selectFrom(bookCategory)
                    .innerJoin(bookCategory.book, book)
                    .where(
                            book.bookKey.eq(bookKey),
                            bookCategory.name.eq(name),
                            bookCategory.parent.eq(parentFlowCategory),
                            bookCategory.status.eq(ACTIVE)
                    )
                    .fetchOne());
        }
        return target;
    }

    @Override
    public boolean findCustomTarget(Category targetRoot, String bookKey, String target) {
        List<BookCategory> categories = jpaQueryFactory.selectFrom(bookCategory)
                .where(
                        bookCategory.name.eq(target),
                        bookCategory.status.eq(ACTIVE)
                )
                .innerJoin(bookCategory.parent, category)
                .where(category.eq(targetRoot))
                .innerJoin(bookCategory.book, book)
                .where(book.bookKey.eq(bookKey))
                .fetch();

        return categories.size() == 1;
    }

    @Override
    @Transactional
    public void inactiveCustomCategory(DeleteCategoryRequest request) {
        Category targetRoot = jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(request.getRoot()),
                category.instanceOf(RootCategory.class))
            .fetchOne();

        jpaQueryFactory.update(bookCategory)
            .set(bookCategory.status,INACTIVE)
            .where(bookCategory.name.eq(request.getName()),
                bookCategory.book.id.eq(
                    JPAExpressions.select(book.id)
                        .from(book)
                        .where(book.bookKey.eq(request.getBookKey()))
                ),
                bookCategory.parent.eq(targetRoot))
            .execute();
    }


    @Override
    public List<BookCategory> findAllCustomCategory(Book book) {
        return jpaQueryFactory.selectFrom(bookCategory)
                .where(
                        bookCategory.book.eq(book),
                        bookCategory.status.eq(ACTIVE)
                )
                .fetch();
    }

    @Override
    public Optional<Category> findParentCategory(String parentName) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(category)
                .where(
                        category.name.eq(parentName),
                        category.instanceOf(RootCategory.class)
                )
                .innerJoin(category)
                .fetchOne());
    }

    @Override
    @Transactional
    public void inactiveAllByBook(final Book book) {
        jpaQueryFactory.update(bookCategory)
                .set(bookCategory.status, INACTIVE)
                .set(bookCategory.updatedAt, LocalDateTime.now())
                .where(
                        bookCategory.book.eq(book),
                        bookCategory.status.eq(ACTIVE)
                )
                .execute();
    }
}
