package com.floney.floney.book;

import com.floney.floney.book.dto.request.SaveRecentBookKeyRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.config.TestConfig;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.common.constant.Status.INACTIVE;
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
    @DisplayName("가계부 코드와 유저 이메일로 BookUser를 조회한다")
    void getBookUser() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser newBookUser = BookUser.builder()
            .book(savedBook)
            .user(savedUser)
            .build();

        bookUserRepository.save(newBookUser);
        BookUser find = bookUserRepository.findBookUserByKey(savedUser.getEmail(), savedBook.getBookKey()).get();
        Assertions.assertThat(find.getBook().getName())
            .isEqualTo(savedBook.getName());
    }

    @Test
    @DisplayName("가계부 사용자가 몇명인지 반환한다")
    void count_book_user() {
        User savedUser = userRepository.save(UserFixture.createUser());
        Book savedBook = bookRepository.save(BookFixture.createBook());

        BookUser owner = BookFixture.createBookUser(savedUser, savedBook);
        BookUser member = BookFixture.createBookUser(savedUser, savedBook);

        bookUserRepository.save(owner);
        bookUserRepository.save(member);

        assertThat(bookUserRepository.countByBookExclusively(savedBook))
            .isEqualTo(2);
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
        User user = UserFixture.createUser();
        SaveRecentBookKeyRequest request = new SaveRecentBookKeyRequest("book-key");
        user.saveRecentBookKey(request.getBookKey());
        User savedUser = userRepository.save(user);
        Assertions.assertThat(savedUser.getRecentBookKey()).isEqualTo("book-key");
    }

}
