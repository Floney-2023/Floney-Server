package com.floney.floney.book.service;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.category.BookCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.floney.floney.book.entity.DefaultCategory.rootParent;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public CategoryResponse createUserCategory(CreateCategoryRequest request) {
        Category parent = rootParent();
        if (request.hasParent()) {
            parent = findParent(request.getParent());
        }
        BookCategory newCategory = categoryRepository.save(request.of(parent, findBook(request.getBookKey())));
        return CategoryResponse.of(newCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllBy(String root, String bookKey) {
        List<Category> categories = categoryRepository.findAllCategory(root, bookKey);
        return CategoryResponse.to(categories);
    }

    @Override
    @Transactional
    public void deleteCustomCategory(String categoryName, String bookKey) {
        categoryRepository.deleteCustomCategory(bookKey, categoryName);

    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(NotFoundBookException::new);
    }

    private Category findParent(String parent) {
        return categoryRepository.findByName(parent)
            .orElseThrow(NotFoundCategoryException::new);
    }

}
