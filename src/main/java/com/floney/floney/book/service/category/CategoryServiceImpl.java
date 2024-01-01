package com.floney.floney.book.service.category;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.Category;
import com.floney.floney.book.domain.entity.category.BookCategory;
import com.floney.floney.book.dto.process.CategoryInfo;
import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.dto.request.DeleteCategoryRequest;
import com.floney.floney.book.dto.response.CreateCategoryResponse;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.service.BookLineService;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.AlreadyExistException;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.floney.floney.book.domain.entity.DefaultCategory.rootParent;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookLineService bookLineService;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    @Override
    @Transactional
    public CreateCategoryResponse createUserCategory(CreateCategoryRequest request) {
        Category parent = rootParent();
        if (request.hasParent()) {
            parent = categoryRepository.findParentCategory(request.getParent())
                .orElseThrow(() -> new NotFoundCategoryException(request.getParent()));
        }

        // 같은 이름으로 카테고리 생성 방지
        categoryRepository.findCustomTarget(parent, request.getBookKey(), request.getName()).ifPresent(saved -> {
            throw new AlreadyExistException(request.getName());
        });

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
        Category root = categoryRepository.findParentCategory(request.getRoot())
            .orElseThrow(() -> new NotFoundCategoryException(request.getRoot()));

        Category category = categoryRepository.findCustomTarget(root, request.getBookKey(), request.getName())
            .orElseThrow(() -> new NotFoundCategoryException((request.getName())));

        categoryRepository.findAllBookLineByCategory(category)
            .forEach((bookLine) -> {
                // 예산, 자산, 이월설정 관련 내역 모두 삭제
                bookLineService.deleteLine(bookLine.getId());
            });

        //카테고리 삭제
        category.inactive();
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteAllBookLineCategory(long bookLineId) {
        bookLineCategoryRepository.inactiveAllByBookLineId(bookLineId);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, Status.ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

}
