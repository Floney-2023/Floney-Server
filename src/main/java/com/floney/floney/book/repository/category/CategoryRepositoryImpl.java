package com.floney.floney.book.repository.category;

import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.process.QCategoryInfo;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.DefaultCategory;
import com.floney.floney.book.entity.RootCategory;
import com.floney.floney.book.entity.category.BookCategory;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QCategory.category;
import static com.floney.floney.book.entity.category.QBookCategory.bookCategory;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private static final boolean DEFAULT = true;
    private static final boolean CUSTOM = false;

    @Override
    public List<CategoryInfo> findAllCategory(String name, String bookKey) {
        Category targetRoot = jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(name),
                category.instanceOf(RootCategory.class))
            .fetchOne();

        List<CategoryInfo> children = jpaQueryFactory.select(
                new QCategoryInfo(Expressions.constant(DEFAULT), category.name))
            .from(category)
            .where(category.parent.eq(targetRoot),
                category.instanceOf(DefaultCategory.class))
            .fetch();

        children.addAll(jpaQueryFactory.select(
                new QCategoryInfo(Expressions.constant(CUSTOM), bookCategory.name))
            .from(bookCategory)
            .innerJoin(bookCategory.parent, category)
            .where(category.eq(targetRoot))
            .innerJoin(bookCategory.book, book)
            .where(book.bookKey.eq(bookKey))
            .fetch());

        return children;
    }

    @Override
    public Category findFlowCategory(String name) {
        return jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(name),
                category.instanceOf(RootCategory.class),
                category.parent.isNull())
            .fetchOne();
    }

    @Override
    public Category findAssetCategory(String name) {
        return jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(name),
                category.instanceOf(DefaultCategory.class))
            .innerJoin(category)
            .where(category.parent.name.eq("자산"))
            .fetchOne();
    }


    @Override
    public Optional<Category> findLineCategory(String name, String bookKey, String parent) {
        Optional<Category> target = Optional.ofNullable(jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(name),
                category.parent.eq(findFlowCategory(parent))
                , category.instanceOf(DefaultCategory.class))
            .fetchOne());

        if (target.isEmpty()) {
            target = Optional.ofNullable(jpaQueryFactory
                .selectFrom(category)
                .innerJoin(bookCategory.book, book)
                .where(book.bookKey.eq(bookKey), bookCategory.name.eq(name),
                    bookCategory.parent.eq(findFlowCategory(parent)))
                .fetchOne());
        }
        return target;
    }

    @Override
    public boolean findCustomTarget(Category targetRoot, String bookKey, String target) {
        List<BookCategory> categories = jpaQueryFactory.selectFrom(bookCategory)
            .where(bookCategory.name.eq(target))
            .innerJoin(bookCategory.parent, category)
            .where(category.eq(targetRoot))
            .innerJoin(bookCategory.book, book)
            .where(book.bookKey.eq(bookKey))
            .fetch();

        return categories.size() == 1;
    }

    @Override
    public void deleteCustomCategory(DeleteCategoryRequest request) {
        Category targetRoot = jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(request.getRoot()),
                category.instanceOf(RootCategory.class))
            .fetchOne();

        jpaQueryFactory.delete(bookCategory)
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
    public Optional<Category> findParentCategory(String parentName) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(parentName),
                category.instanceOf(RootCategory.class))
            .innerJoin(category)
            .fetchOne());
    }

}
