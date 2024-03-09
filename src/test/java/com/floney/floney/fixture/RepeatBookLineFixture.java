package com.floney.floney.fixture;

import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;

import java.time.LocalDate;

public class RepeatBookLineFixture {

    public static RepeatBookLine createRepeatBookLine(Category category, BookUser bookUser, Book book, RepeatDuration repeatDuration) {
        return RepeatBookLine.builder()
            .lineDate(LocalDate.now())
            .lineCategory(category)
            .lineSubcategory(SubcategoryFixture.createSubcategoryWithId(book, CategoryFixture.create(CategoryType.INCOME), "급여", 1L))
            .assetSubcategory(SubcategoryFixture.createSubcategoryWithId(book, CategoryFixture.create(CategoryType.OUTCOME), "은행", 2L))
            .money(1000.0)
            .repeatDuration(repeatDuration)
            .book(book)
            .writer(bookUser)
            .build();
    }
}
