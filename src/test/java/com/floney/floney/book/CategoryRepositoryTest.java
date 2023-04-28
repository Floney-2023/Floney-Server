package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookCategory;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookCategoryRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.CategoryRepository;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.floney.floney.book.CategoryFixture.ROOT;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    Book savedBook;
    Book savedBook2;
    Category savedRoot;


    @BeforeEach
    void init() {
        savedBook = bookRepository.save(BookFixture.createBookWith(1L));
        savedBook2 = bookRepository.save(BookFixture.createBookWith(2L));
        savedRoot = categoryRepository.save(CategoryFixture.createRootCategory());
    }

    @Test
    @DisplayName("가계부마다 카테고리를 추가할 수 있다")
    void save_category() {
        Category child = Category.builder()
            .parent(savedRoot)
            .name("루트자식")
            .build();

        categoryRepository.save(child);
        BookCategory bookCategory = BookCategory.builder()
            .parent(savedRoot)
            .name("커스텀자식1")
            .book(savedBook)
            .build();

        BookCategory bookCategory2 = BookCategory.builder()
            .parent(savedRoot)
            .name("커스텀자식2")
            .book(savedBook)
            .build();

        bookCategoryRepository.save(bookCategory);
        bookCategoryRepository.save(bookCategory2);

        Assertions.assertThat(categoryRepository.findAllCategory(savedRoot)
            .size()).isEqualTo(1);
        Assertions.assertThat(categoryRepository.findCustom(savedRoot, savedBook.getBookKey())
            .size()).isEqualTo(2);

    }


}
