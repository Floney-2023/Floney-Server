package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;

import java.util.List;

public interface CategoryService {

    CreateCategoryResponse createUserCategory(CreateCategoryRequest request);

    List<CategoryInfo> findAllBy(String root, String bookKey);

    void deleteCustomCategory(DeleteCategoryRequest request);
}
