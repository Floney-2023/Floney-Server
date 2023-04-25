package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.CategoryRepository;
import com.floney.floney.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    Book savedBook;

    @BeforeEach
    void init() {
        savedBook = bookRepository.save(BookFixture.createBookWith(1L));
    }

    @Test
    @DisplayName("부모 카테고리와 자식 카테고리를 저장하고, 조회한다")
    void category_find() {
        Category parentCategory = Category.builder()
            .book(savedBook)
            .name("부모카테고리")
            .build();

        Category savedParent = categoryRepository.save(parentCategory);

        Category childCategory = Category.builder()
            .book(savedBook)
            .name("자식카테고리1")
            .parent(savedParent)
            .build();

        Category childCategory2 = Category.builder()
            .book(savedBook)
            .name("자식카테고리2")
            .parent(savedParent)
            .build();


        Category child = categoryRepository.save(childCategory);
        Category child2 = categoryRepository.save(childCategory2);

        savedParent.addChildren(child);
        savedParent.addChildren(child2);

        categoryRepository.save(savedParent);
        Category findParent = categoryRepository.findByNameAndBook("부모카테고리", savedBook)
            .get();

        List<String> childNames = findParent.getChildren()
            .stream()
            .map(c -> c.getName())
            .collect(Collectors.toList());

        assertThat(childNames)
            .isEqualTo(asList("자식카테고리1", "자식카테고리2"));

    }

}
