package com.floney.floney.book.service;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.book.dto.CreateCategoryRequest;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

}
