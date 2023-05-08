package com.floney.floney.book.controller;

import com.floney.floney.book.dto.CreateCategoryRequest;
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

    @PostMapping("/books/categories")
    public ResponseEntity<?> crateCategory(@RequestBody CreateCategoryRequest request) {
        return new ResponseEntity<>(categoryService.createUserCategory(request), HttpStatus.CREATED);
    }

    @GetMapping("/books/categories")
    public ResponseEntity<?> findAllBy(@RequestParam String bookKey,
                                    @RequestParam String root) {
        return new ResponseEntity<>(categoryService.findAllBy(root, bookKey), HttpStatus.ACCEPTED);
    }

}
