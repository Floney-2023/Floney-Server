package com.floney.floney.book;

import static com.floney.floney.book.CategoryFixture.ROOT;
import static com.floney.floney.book.CategoryFixture.categoryChildResponse;
import static com.floney.floney.book.CategoryFixture.categoryRootResponse;
import static com.floney.floney.book.CategoryFixture.createBookCategory;
import static com.floney.floney.book.CategoryFixture.createChildCategory;
import static com.floney.floney.book.CategoryFixture.createDefaultRoot;
import static com.floney.floney.book.CategoryFixture.createRootCategory;
import static com.floney.floney.book.CategoryFixture.createRootRequest;
import static com.floney.floney.fixture.BookFixture.BOOK_KEY;
import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.floney.floney.book.dto.request.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.RootCategory;
import com.floney.floney.book.entity.category.BookCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.service.CategoryServiceImpl;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import com.floney.floney.fixture.BookFixture;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        RootCategory rootCategory = createDefaultRoot("ROOT");

        given(categoryRepository.findParentCategory(ROOT))
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
        RootCategory rootCategory = createDefaultRoot("ROOT");

        given(categoryRepository.findParentCategory(ROOT))
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
        given(categoryRepository.findParentCategory(ROOT))
            .willReturn(Optional.empty());

        CreateCategoryRequest request = createBookCategory();
        Assertions.assertThatThrownBy(() -> categoryService.createUserCategory(request))
            .isInstanceOf(NotFoundCategoryException.class);
    }
}
