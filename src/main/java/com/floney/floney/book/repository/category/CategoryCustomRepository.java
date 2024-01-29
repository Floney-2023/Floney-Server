package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.Category;
import com.floney.floney.book.domain.category.CustomSubCategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CategoryInfo;

import java.util.List;
import java.util.Optional;

public interface CategoryCustomRepository {

    List<CategoryInfo> findAllCategory(String parentName, String bookKey);

    Optional<Category> findLineCategory(String name);

    Optional<CustomSubCategory> findAssetSubCategory(String name, Book book);

    Optional<CustomSubCategory> findLineSubCategory(String name, Book book, Category parent);

    List<BookLine> findAllBookLineByCategory(CustomSubCategory subCategory);

    Optional<CustomSubCategory> findCustomTarget(Category parent, Book targetBook, String name);

    List<CustomSubCategory> findAllLineSubCategoryByLineCategory(Category parent, String bookKey);

    Optional<Category> findParentCategory(String name);

    void inactiveAllByBook(Book book);
}
