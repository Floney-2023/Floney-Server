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

    List<CategoryInfo> findAllSubCategoryInfoByParent(CategoryType parent, String bookKey);

    Optional<Category> findLineCategory(CategoryType categoryType);

    Optional<Subcategory> findLineSubCategory(String name, Book book, CategoryType parent);

    List<BookLine> findAllBookLineBySubCategory(Subcategory subCategory);

    Optional<Subcategory> findCustomCategory(Category parent, Book targetBook, String name);

    List<Subcategory> findAllSubCategoryByLineCategory(Category parent, String bookKey);

    void inactiveAllByBook(Book book);
}
