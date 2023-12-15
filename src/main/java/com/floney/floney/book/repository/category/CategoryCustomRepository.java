package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.domain.entity.category.BookCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryCustomRepository {

    List<CategoryInfo> findAllCategory(String name, String bookKey);

    Optional<Category> findFlowCategory(String name);

    Optional<Category> findAssetCategory(String name, String bookKey);

    Optional<Category> findLineCategory(String name, String bookKey, String parent);

    List<BookLine> findAllBookLineByCategory(Category category);

    Optional<Category> findCustomTarget(Category targetRoot, String bookKey, String target);

    void inactiveCustomCategory(DeleteCategoryRequest request);

    List<BookCategory> findAllCustomCategory(Book book);

    List<Category> findAllDefaultChildCategoryByRoot(Category root);

    List<BookCategory> findAllCustomChildCategoryByRootAndRoot(Category root, String bookKey);

    Optional<Category> findParentCategory(String parentName);

    void inactiveAllByBook(Book book);
}
