package com.floney.floney.book.service;

import com.floney.floney.book.dto.CategoryResponse;
import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.DefaultCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.CategoryRepository;
import com.floney.floney.book.repository.BookCategoryRepository;
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
        List<Category> categories = categoryRepository.findAllCategory(root,bookKey);
        return CategoryResponse.to(categories);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKey(bookKey)
            .orElseThrow(NotFoundBookException::new);
    }

    private Category findParent(String parent) {
        return categoryRepository.findByName(parent)
            .orElseThrow(NotFoundCategoryException::new);
    }

    private void isDuplicate(Category root, CreateCategoryRequest request) {
        if (!categoryRepository.findCustomTarget(root, request.getBookKey(), request.getName())) {
            //에러 던지기
        }
        ;
    }

}
