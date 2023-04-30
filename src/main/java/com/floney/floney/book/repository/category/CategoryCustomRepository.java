package com.floney.floney.book.repository.category;

import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;

import java.util.List;

public interface CategoryCustomRepository {


    List<Category> findAllCategory(String name, String bookKey);

    boolean findCustomTarget(Category targetRoot, String bookKey, String target);
}
