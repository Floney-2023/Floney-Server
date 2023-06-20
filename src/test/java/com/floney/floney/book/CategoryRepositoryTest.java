package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.DefaultCategory;
import com.floney.floney.book.entity.RootCategory;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.config.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.floney.floney.book.CategoryFixture.CHILD;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    Book savedBook;
    RootCategory savedRoot;


    @BeforeEach
    void init() {
        savedBook = bookRepository.save(BookFixture.createBookWith("1234"));
        savedRoot = categoryRepository.save(CategoryFixture.createDefaultRoot("ROOT"));
    }

    @Test
    @DisplayName("가게부만의 카테고리를 추가할 수 있다")
    void save_category() {
        Book savedBook2 = bookRepository.save(BookFixture.createBookWith("2222"));
        categoryRepository.save(CategoryFixture.createChildCategory(savedRoot, savedBook));
        categoryRepository.save(CategoryFixture.createChildCategory(savedRoot, savedBook));
        Assertions.assertThat(categoryRepository.findAllCategory("ROOT", savedBook.getBookKey())
            .size()).isEqualTo(2);
        Assertions.assertThat(categoryRepository.findAllCategory("ROOT", savedBook2.getBookKey())
            .size()).isEqualTo(0);
    }

    @Test
    @DisplayName("공통 가계부의 자식 카테고리들을 모두 조회한다")
    void find_all_default() {
        Category root = categoryRepository.save(CategoryFixture.createDefaultRoot("ROOT1"));

        categoryRepository.save(CategoryFixture.createDefaultChild(root, "CHILD1"));
        categoryRepository.save(CategoryFixture.createDefaultChild(root, "CHILD2"));

        Assertions.assertThat(categoryRepository.findAllCategory("ROOT1", "book").size())
            .isEqualTo(2);
    }

    @Test
    @DisplayName("가계부만의 카테고리 추가 시 중복을 체크한다")
    void is_duplicate() {
        categoryRepository.save(CategoryFixture.createChildCategory(savedRoot, savedBook));
        categoryRepository.save(CategoryFixture.createChildCategory(savedRoot, savedBook));

        Assertions.assertThat(categoryRepository.findCustomTarget(savedRoot, savedBook.getBookKey(), CHILD))
            .isFalse();
    }

    @Test
    @DisplayName("커스텀 카테고리를 삭제한다")
    void delete_custom() {
        categoryRepository.save(CategoryFixture.createChildCategory(savedRoot, savedBook));
        categoryRepository.deleteCustomCategory(savedBook.getBookKey(),CHILD);
        Assertions.assertThat(categoryRepository.findByName(CHILD).isEmpty()).isTrue();
    }


}
