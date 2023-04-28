package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;
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
    public List<Category> findAllCategory(Category targetRoot) {
        List<Category> children = jpaQueryFactory.selectFrom(category)
            .where(category.parent.eq(targetRoot))
            .fetch();
        return children;
    }

    @Override
    public List<BookCategory> findCustom(Category targetRoot, String bookKey) {
        return jpaQueryFactory.selectFrom(bookCategory)
            .innerJoin(bookCategory.parent, category)
            .where(category.eq(targetRoot))
            .innerJoin(bookCategory.book, book)
            .where(book.bookKey.eq(bookKey))
            .fetch();
    }
}
