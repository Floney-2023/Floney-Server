package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.NoAuthorityException;
import com.floney.floney.config.TestConfig;
import com.floney.floney.config.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("가계부 코드와 닉네임을 동시에 만족하는 BookUser를 조회한다")
    void getBookUser() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser newBookUser = BookUser.builder()
            .book(savedBook)
            .user(savedUser)
            .build();

        bookUserRepository.save(newBookUser);
        BookUser find = bookUserRepository.findUserWith(savedUser.getNickname(), savedBook.getBookKey()).get();
        Assertions.assertThat(find.getBook().getName())
            .isEqualTo(savedBook.getName());
    }

    @Test
    @DisplayName("가계부 사용자가 2명이상이면 가계부를 삭제할 수 없다")
    void delete_exception() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);
        BookUser member = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);
        bookUserRepository.save(member);

        assertThatThrownBy(() -> bookUserRepository.countBookUser(savedBook))
            .isInstanceOf(NoAuthorityException.class);
    }

    @Test
    @DisplayName("email과 Book으로 bookUser를 찾는다")
    void findUser() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);

        Assertions.assertThat(bookUserRepository.findByEmailAndBook(savedUser.getEmail(), savedBook))
            .isEqualTo(owner);
    }

    @Test
    @DisplayName("status가 true인 bookUser만 조회한다")
    void active_user() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);

        Assertions.assertThat(bookUserRepository.countBookUserByUserAndStatus(savedUser, true))
            .isEqualTo(1);
    }

    @Test
    @DisplayName("status가 false인 bookUser는 조회하지 않는다")
    void inActive_user() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);

        Assertions.assertThat(bookUserRepository.countBookUserByUserAndStatus(savedUser, false))
            .isEqualTo(0);
    }
}
