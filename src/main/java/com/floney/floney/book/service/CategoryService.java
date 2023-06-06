package com.floney.floney.book.service;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.book.dto.CreateCategoryRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {

    CategoryResponse createUserCategory(CreateCategoryRequest request);

    List<CategoryResponse> findAllBy(String root, String bookKey);

    void deleteCustomCategory(String categoryName, String bookKey);
}
