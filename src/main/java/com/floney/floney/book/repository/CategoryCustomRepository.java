package com.floney.floney.book.repository;

import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;

import java.util.List;

public interface CategoryCustomRepository {
    List<Category> findAllCategory(Category root);

    List<BookCategory> findCustom(Category targetRoot, String bookKey);
}
