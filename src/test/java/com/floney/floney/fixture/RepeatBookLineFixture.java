package com.floney.floney.fixture;

import com.floney.floney.book.domain.RepeatDuration;
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
            .lineSubcategory(SubcategoryFixture.createSubcategoryWithId(book, CategoryFixture.incomeCategory(), "급여", 1L))
            .assetSubcategory(SubcategoryFixture.createSubcategoryWithId(book, CategoryFixture.assetCategory(), "은행", 2L))
            .money(1000.0)
            .repeatDuration(repeatDuration)
            .book(book)
            .writer(bookUser)
            .build();
    }
}
