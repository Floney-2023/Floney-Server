package com.floney.floney.book.controller;

import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 커스텀 카테고리 생성
     *
     * @return CreateCategoryResponse 생성된 커스텀 카테고리 정보
     * @body CreateCategoryRequest 커스텀 카테고리 생성용 기본 정보
     */
    @PostMapping("/books/categories")
    public ResponseEntity<?> crateCategory(@RequestBody CreateCategoryRequest request) {
        return new ResponseEntity<>(categoryService.createUserCategory(request), HttpStatus.CREATED);
    }

    /**
     * 카테고리 조회하기
     *
     * @param bookKey 가계부 식별키
     * @param root    부모 카테고리
     * @return List<CategoryInfo> 부모와 연관된 모든 자식 카테고리
     */
    @GetMapping("/books/categories")
    public ResponseEntity<?> findAllBy(@RequestParam String bookKey,
                                       @RequestParam String root) {
        return new ResponseEntity<>(categoryService.findAllBy(root, bookKey), HttpStatus.OK);
    }

    /**
     * 커스텀 카테고리 삭제하기
     *
     * @body DeleteCategoryRequest 삭제할 카테고리 정보
     */
    @DeleteMapping("/books/categories")
    public ResponseEntity<?> deleteCategory(@RequestBody DeleteCategoryRequest request) {
        categoryService.deleteCustomCategory(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
