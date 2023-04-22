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

import java.util.UUID;

import static com.floney.floney.book.entity.Category.rootParent;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
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
        return CategoryResponse.of(newCategory);
    }

    @Override
    public CategoryResponse findAllBy(String root, String bookKey) {
        Book book = findBook(bookKey);
        Category rootCategory = categoryRepository.findByNameAndBook(root, book)
            .orElseThrow(NotFoundCategoryException::new);
        return CategoryResponse.of(rootCategory);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKey(UUID.fromString(bookKey))
            .orElseThrow(NotFoundBookException::new);
    }

    private Category findParent(String parent) {
        return categoryRepository.findByName(parent)
            .orElseThrow(NotFoundCategoryException::new);
    }
}
