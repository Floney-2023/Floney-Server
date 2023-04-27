package com.floney.floney.book;

import com.floney.floney.book.dto.CreateCategoryRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.CategoryRepository;
import com.floney.floney.book.service.CategoryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.floney.floney.book.BookFixture.BOOK_KEY;
import static com.floney.floney.book.CategoryFixture.ROOT;
import static java.util.Optional.ofNullable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category rootCategory;

    @BeforeEach
    @Test
    @DisplayName("루트 카테고리를 생성한다")
    void create_root_category() {
        CreateCategoryRequest request = CategoryFixture.createRootRequest();
        Book savedBook = BookFixture.createBookWith(1L);

        given(bookRepository.findBookByBookKey(BOOK_KEY))
            .willReturn(ofNullable(savedBook));

        rootCategory = CategoryFixture.createRootCategory(savedBook);
        given(categoryRepository.save(rootCategory))
            .willReturn(rootCategory);

        Assertions.assertThat(categoryService.createCategory(request).getName())
            .isEqualTo(ROOT);
    }
}
