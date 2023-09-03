package com.floney.floney.book.repository.category;

import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.category.BookCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryCustomRepository {
    List<CategoryInfo> findAllCategory(String name, String bookKey);

    Category findFlowCategory(String name);

    Category findAssetCategory(String name);

    Optional<Category> findLineCategory(String name, String bookKey, String parent);

    boolean findCustomTarget(Category targetRoot, String bookKey, String target);

    void deleteCustomCategory(DeleteCategoryRequest request);

    List<BookCategory> findAllCustomCategory(Book book);

    Optional<Category> findParentCategory(String parentName);

}
