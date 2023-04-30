package com.floney.floney.book;

import com.floney.floney.User.UserFixture;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.config.TestConfig;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class BookUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookUserRepository bookUserRepository;

    @Test
    @DisplayName("가계부 코드와 이메일을 동시에 만족하는 BookUser를 조회한다")
    void getBookUser() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser newBookUser = BookUser.builder()
            .book(savedBook)
            .user(savedUser)
            .build();

        bookUserRepository.save(newBookUser);
        BookUser find = bookUserRepository.findUserWith(savedUser.getEmail(), savedBook.getBookKey());
        Assertions.assertThat(find.getBook().getName())
            .isEqualTo(savedBook.getName());
    }
}
