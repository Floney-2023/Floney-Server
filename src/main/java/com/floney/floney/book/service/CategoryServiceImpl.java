package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.category.BookCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
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
    public CreateCategoryResponse createUserCategory(CreateCategoryRequest request) {
        Category parent = rootParent();
        if (request.hasParent()) {
            parent = categoryRepository.findParentCategory(request.getParent())
                    .orElseThrow(() -> new NotFoundCategoryException(request.getParent()));
        }
        BookCategory newCategory = categoryRepository.save(request.of(parent, findBook(request.getBookKey())));
        return CreateCategoryResponse.of(newCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo> findAllBy(String root, String bookKey) {
        return categoryRepository.findAllCategory(root, bookKey);
    }

    @Override
    @Transactional
    public void deleteCustomCategory(DeleteCategoryRequest request) {
        categoryRepository.inactiveCustomCategory(request);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

}
