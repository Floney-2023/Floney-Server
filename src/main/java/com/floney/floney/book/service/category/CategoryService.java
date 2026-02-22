package com.floney.floney.book.service.category;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;

import java.util.List;

public interface CategoryService {

    CreateCategoryResponse createSubcategory(String bookKey, CreateCategoryRequest request);

    List<CategoryInfo> findAllSubcategoriesByCategory(String bookKey, CategoryType parent);

    void deleteSubcategory(String bookKey, DeleteCategoryRequest request);

    void deleteAllBookLineCategory(long bookLineId);
}
