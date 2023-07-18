package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.common.NoAuthorityException;
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

import java.util.Optional;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        BookUser find = bookUserRepository.findBookUserByKey(savedUser.getNickname(), savedBook.getBookKey()).get();
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

        Assertions.assertThat(bookUserRepository.findBookUserBy(savedUser.getEmail(), savedBook))
            .isEqualTo(owner);
    }

    @Test
    @DisplayName("status가 true인 bookUser만 조회한다")
    void active_user() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);

        Assertions.assertThat(bookUserRepository.countBookUserByUserAndStatus(savedUser, ACTIVE))
            .isEqualTo(1);
    }

    @Test
    @DisplayName("status가 false인 bookUser는 조회하지 않는다")
    void inActive_user() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);

        Assertions.assertThat(bookUserRepository.countBookUserByUserAndStatus(savedUser, INACTIVE))
            .isEqualTo(0);
    }

    @Test
    @DisplayName("가계부의 모든 멤버를 조회한다")
    void allUser() {
        Book savedBook = bookRepository.save(BookFixture.createBook());

        User user1 = userRepository.save(UserFixture.createUser());
        BookUser bookUser1 = BookFixture.createBookUser(user1, savedBook);

        User user2 = userRepository.save(UserFixture.createUser2());
        BookUser bookUser2 = BookFixture.createBookUser(user2, savedBook);

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);

        assertThat(bookUserRepository.findAllUser(savedBook.getBookKey()).size())
            .isEqualTo(2);

    }

    @Test
    @DisplayName("내가 속한 가계부 중 가장 최근에 업데이트를 한 가계부를 조회한다")
    void my_exist_book() {
        Book savedBook = bookRepository.save(BookFixture.createBook());
        Book savedBook2 = bookRepository.save(BookFixture.createBookWith("BOOK2"));

        User user1 = userRepository.save(UserFixture.createUser());

        BookUser bookUser1 = BookFixture.createBookUser(user1, savedBook);
        BookUser bookUser2 = BookFixture.createBookUser(user1, savedBook2);

        bookUserRepository.save(bookUser1);
        bookUserRepository.save(bookUser2);
        Assertions.assertThat(bookUserRepository.findBookBy(user1.getEmail()).get())
            .isEqualTo(savedBook2);

    }

    @Test
    @DisplayName("내가 속한 가계부가 없을 경우 optional empty를 반환한다")
    void my_non_exist_book() {
        User user1 = userRepository.save(UserFixture.createUser());

        Assertions.assertThat(bookUserRepository.findBookBy(user1.getEmail()))
            .isEqualTo(Optional.empty());

    }
}
