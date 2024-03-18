package com.floney.floney.fixture;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLineCategory;

public class BookLineCategoryFixture {

    public static BookLineCategory incomeBookLineCategory(final Book book,
                                                          final String lineSubCategoryName,
                                                          final String assetSubCategoryName) {
        final Category lineCategory = CategoryFixture.create(CategoryType.INCOME);
        final Category assetLineCategory = CategoryFixture.create(CategoryType.ASSET);

        final Subcategory subcategory = SubcategoryFixture.createSubcategory(book, lineCategory, lineSubCategoryName);
        final Subcategory assetSubcategory = SubcategoryFixture.createSubcategory(book, assetLineCategory, assetSubCategoryName);

        return BookLineCategory.create(lineCategory, subcategory, assetSubcategory);
    }

    public static BookLineCategory outcomeBookLineCategory(final Book book,
                                                           final String lineSubCategoryName,
                                                           final String assetSubCategoryName) {
        final Category lineCategory = CategoryFixture.create(CategoryType.OUTCOME);
        final Category assetLineCategory = CategoryFixture.create(CategoryType.ASSET);

        final Subcategory subCategory = SubcategoryFixture.createSubcategory(book, lineCategory, lineSubCategoryName);
        final Subcategory assetSubCategory = SubcategoryFixture.createSubcategory(book, assetLineCategory, assetSubCategoryName);

        return BookLineCategory.create(lineCategory, subCategory, assetSubCategory);
    }

    public static BookLineCategory transferBookLineCategory(final Book book,
                                                            final String lineSubCategoryName,
                                                            final String assetSubCategoryName) {
        final Category lineCategory = CategoryFixture.create(CategoryType.TRANSFER);
        final Category assetCategory = CategoryFixture.create(CategoryType.ASSET);

        final Subcategory subcategory = SubcategoryFixture.createSubcategory(book, lineCategory, lineSubCategoryName);
        final Subcategory assetSubcategory = SubcategoryFixture.createSubcategory(book, assetCategory, assetSubCategoryName);

        return BookLineCategory.create(lineCategory, subcategory, assetSubcategory);
    }

}
