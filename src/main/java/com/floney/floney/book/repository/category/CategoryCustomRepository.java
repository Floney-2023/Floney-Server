package com.floney.floney.book.repository.category;

import com.floney.floney.book.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryCustomRepository {
    List<Category> findAllCategory(String name, String bookKey);

    Category findFlowCategory(String name);

    Category findAssetCategory(String name);

    Optional<Category> findLineCategory(String name, String bookKey, String parent);

    boolean findCustomTarget(Category targetRoot, String bookKey, String target);

    void deleteCustomCategory(String bookKey, String targetName);

    Optional<Category> findParentCategory(String parentName);
}
