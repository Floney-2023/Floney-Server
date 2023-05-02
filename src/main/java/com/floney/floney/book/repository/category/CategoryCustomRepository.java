package com.floney.floney.book.repository.category;

import com.floney.floney.book.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryCustomRepository {
    List<Category> findAllCategory(String name, String bookKey);

    Optional<Category> findCategory(String name, String bookKey);

    boolean findCustomTarget(Category targetRoot, String bookKey, String target);
}
