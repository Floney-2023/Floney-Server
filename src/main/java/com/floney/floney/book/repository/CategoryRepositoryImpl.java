package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.DefaultCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.floney.floney.book.entity.QBook.book;
import static com.floney.floney.book.entity.QBookCategory.bookCategory;
import static com.floney.floney.book.entity.QCategory.category;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Category> findAllCategory(String name, String bookKey) {
        Category targetRoot = jpaQueryFactory.selectFrom(category)
            .where(category.name.eq(name), category.instanceOf(DefaultCategory.class))
            .fetchOne();

        List<Category> children = jpaQueryFactory.selectFrom(category)
            .where(category.parent.eq(targetRoot), category.instanceOf(DefaultCategory.class))
            .fetch();

        children.addAll(jpaQueryFactory.selectFrom(bookCategory)
            .innerJoin(bookCategory.parent, category)
            .where(category.eq(targetRoot))
            .innerJoin(bookCategory.book, book)
            .where(book.bookKey.eq(bookKey))
            .fetch());
        return children;
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
}
