package com.floney.floney.book;

import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.category.BookCategory;
import com.floney.floney.book.entity.DefaultCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.service.CategoryServiceImpl;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotFoundCategoryException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.floney.floney.book.BookFixture.BOOK_KEY;
import static com.floney.floney.book.CategoryFixture.*;
import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("사용자가 루트 카테고리를 생성한다")
    void create_root_category() {
        CreateCategoryRequest request = createRootRequest();
        Book savedBook = BookFixture.createBookWith("1234");

        given(bookRepository.findBookByBookKeyAndStatus(BOOK_KEY, Status.ACTIVE))
            .willReturn(ofNullable(savedBook));

        BookCategory rootCategory = createRootCategory(savedBook);

        given(categoryRepository.save(any(BookCategory.class)))
            .willReturn(rootCategory);

        Assertions.assertThat(categoryService.createUserCategory(request))
            .isEqualTo(categoryRootResponse());

    }

    @Test
    @DisplayName("사용자가 자식 카테고리를 추가한다")
    void custom_category() {
        Book savedBook = BookFixture.createBookWith("1234");
        DefaultCategory rootCategory = createDefaultRoot("ROOT");

        given(categoryRepository.findByName(ROOT))
            .willReturn(ofNullable(rootCategory));

        given(bookRepository.findBookByBookKeyAndStatus(BOOK_KEY,Status.ACTIVE))
            .willReturn(ofNullable(savedBook));

        given(categoryRepository.save(any(BookCategory.class)))
            .willReturn(createChildCategory(rootCategory,savedBook));

        CreateCategoryRequest request = createBookCategory();
        Assertions.assertThat(categoryService.createUserCategory(request)).isEqualTo(categoryChildResponse());
    }

    @Test
    @DisplayName("가계부를 찾을 수 없으면 NotFoundBookException을 발생한다")
    void not_found_book() {
        DefaultCategory rootCategory = createDefaultRoot("ROOT");

        given(categoryRepository.findByName(ROOT))
            .willReturn(ofNullable(rootCategory));

        given(bookRepository.findBookByBookKeyAndStatus(BOOK_KEY,Status.ACTIVE))
            .willReturn(Optional.empty());

        CreateCategoryRequest request = createBookCategory();
        Assertions.assertThatThrownBy(() -> categoryService.createUserCategory(request))
            .isInstanceOf(NotFoundBookException.class);
    }

    @Test
    @DisplayName("루트 카테고리를 찾을 수 없으면 NotFoundCategoryException을 발생한다")
    void not_found_root() {
        given(categoryRepository.findByName(ROOT))
            .willReturn(Optional.empty());

        CreateCategoryRequest request = createBookCategory();
        Assertions.assertThatThrownBy(() -> categoryService.createUserCategory(request))
            .isInstanceOf(NotFoundCategoryException.class);
    }
}
