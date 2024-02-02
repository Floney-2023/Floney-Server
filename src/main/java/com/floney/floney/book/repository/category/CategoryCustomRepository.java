package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CategoryInfo;

import java.util.List;
import java.util.Optional;

public interface CategoryCustomRepository {

    List<CategoryInfo> findAllSubCategoryByCategoryType(CategoryType categoryType, String bookKey);

    Optional<Category> findLineCategory(CategoryType categoryType);

    Optional<Subcategory> findAssetSubCategory(String name, Book book);

    Optional<Subcategory> findLineSubCategory(String name, Book book, Category parent);

    List<BookLine> findAllBookLineByCategory(Subcategory subCategory);

    Optional<Subcategory> findCustomTarget(Category parent, Book targetBook, String name);

    List<Subcategory> findAllLineSubCategoryByLineCategory(Category parent, String bookKey);

    Optional<Category> findParentCategory(String name);

    void inactiveAllByBook(Book book);
}
