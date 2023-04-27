package com.floney.floney.book.service;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.CategoryRepository;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.floney.floney.book.entity.Category.rootParent;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category parent = rootParent();
        Book book = findBook(request.getBookKey());

        if (request.hasParent()) {
            parent = findParent(request.getParent());
        }

        Category newCategory = categoryRepository.save(request.of(book, parent));

        if (parent.isNotRoot()) {
            parent.addChildren(newCategory);
            categoryRepository.save(parent);
        }
        return CategoryResponse.of(newCategory, request.getBookKey());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findAllBy(String root, String bookKey) {
        Category rootCategory = categoryRepository.findByName(root)
            .orElseThrow(NotFoundCategoryException::new);
        return CategoryResponse.of(rootCategory,bookKey);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKey(bookKey)
            .orElseThrow(NotFoundBookException::new);
    }

    private Category findParent(String parent) {
        return categoryRepository.findByName(parent)
            .orElseThrow(NotFoundCategoryException::new);
    }
}
