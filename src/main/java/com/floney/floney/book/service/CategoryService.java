package com.floney.floney.book.service;

import com.floney.floney.book.dto.CategoryInfo;
import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.dto.CreateCategoryResponse;
import com.floney.floney.book.dto.DeleteCategoryRequest;

import java.util.List;

public interface CategoryService {

    CreateCategoryResponse createUserCategory(CreateCategoryRequest request);

    List<CategoryInfo> findAllBy(String root, String bookKey);

    void deleteCustomCategory(DeleteCategoryRequest request);
}
