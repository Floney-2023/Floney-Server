package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLineCategory;

public class BookLineCategoryFixture {
    public static BookLineCategory incomeBookLineCategory(Book book, String lineSubCategoryName, String assetSubCategoryName) {
        Category lineCategory = Category.builder().name(CategoryType.INCOME).build();
        Category assetLineCategory = Category.builder().name(CategoryType.ASSET).build();

        Subcategory subCategory = SubcategoryFixture.createSubcategory(book, lineCategory, lineSubCategoryName);
        Subcategory assetSubCategory = SubcategoryFixture.createSubcategory(book, assetLineCategory, assetSubCategoryName);
        return BookLineCategory.create(lineCategory, subCategory, assetSubCategory);
    }
}
