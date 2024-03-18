package com.floney.floney.fixture;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;

import java.time.LocalDate;

public class RepeatBookLineFixture {

    public static RepeatBookLine repeatBookLine(final Category category,
                                                final BookUser bookUser,
                                                final RepeatDuration repeatDuration) {
        final Book book = bookUser.getBook();

        return RepeatBookLine.builder()
            .lineDate(LocalDate.now())
            .lineCategory(category)
            .lineSubcategory(SubcategoryFixture.createSubcategoryWithId(book, category, "급여", 1L))
            .assetSubcategory(SubcategoryFixture.createSubcategoryWithId(book, CategoryFixture.create(CategoryType.ASSET), "은행", 2L))
            .money(1000.0)
            .repeatDuration(repeatDuration)
            .book(book)
            .writer(bookUser)
            .build();
    }

    public static RepeatBookLine repeatBookLine(final RepeatDuration repeatDuration,
                                                final BookUser bookUser,
                                                final Subcategory lineSubcategory,
                                                final Subcategory assetSubcategory) {
        return RepeatBookLine.builder()
            .lineDate(LocalDate.now())
            .lineCategory(lineSubcategory.getParent())
            .lineSubcategory(lineSubcategory)
            .assetSubcategory(assetSubcategory)
            .money(1000.0)
            .repeatDuration(repeatDuration)
            .book(bookUser.getBook())
            .writer(bookUser)
            .build();
    }
}
