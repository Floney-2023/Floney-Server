package com.floney.floney.book.controller;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import com.floney.floney.book.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 생성
     *
     * @param bookKey 가계부 식별키
     * @return CreateCategoryResponse 생성된 카테고리 정보
     * @body CreateCategoryRequest 생성된 카테고리 정보
     */
    @PostMapping("/books/{key}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCategoryResponse createSubcategory(@PathVariable("key") final String bookKey,
                                                    @RequestBody final CreateCategoryRequest request) {
        return categoryService.createSubcategory(bookKey, request);
    }

    /**
     * 카테고리 조회
     *
     * @param bookKey 가계부 식별키
     * @param parent  부모 카테고리 (INCOME, OUTCOME, TRANSFER, ASSET)
     * @return List<CategoryInfo> 부모와 연관된 모든 자식 카테고리
     */
    @GetMapping("/books/{key}/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryInfo> findAllSubcategoriesByCategory(@PathVariable("key") final String bookKey,
                                                             @RequestParam final CategoryType parent) {
        return categoryService.findAllSubcategoriesByCategory(bookKey, parent);
    }

    /**
     * 카테고리 삭제
     *
     * @param bookKey 가계부 식별키
     * @body DeleteCategoryRequest 삭제할 카테고리 정보
     */
    @DeleteMapping("/books/{key}/categories")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSubcategory(@PathVariable("key") final String bookKey,
                                  @RequestBody final DeleteCategoryRequest request) {
        categoryService.deleteSubcategory(bookKey, request);
    }
}
