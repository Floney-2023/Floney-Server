package com.floney.floney.book.repository;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QueryDslTest
@DisplayName("단위 테스트: BookCustomRepository")
class BookCustomRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookUserRepository bookUserRepository;

    @Nested
    @DisplayName("findByBookUserEmailAndBookKey()를 실행할 때")
    class Describe_FindByBookUserEmailAndBookKey {

        @Nested
        @DisplayName("존재하는 bookUser의 email과 bookKey를 입력한 경우")
        class Context_With_ValidBookUserEmailAndBookKey {

            final Book book = BookFixture.createBook();
            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                bookRepository.save(book);
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("book을 반환한다.")
            void it_returns_book() {
                assertThat(bookRepository.findByBookUserEmailAndBookKey(user.getEmail(), book.getBookKey()))
                    .hasValue(book);
            }
        }

        @Nested
        @DisplayName("user가 존재하지 않는 경우")
        class Context_With_NoUser {

            final Book book = BookFixture.createBook();
            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                bookRepository.save(book);
                bookUserRepository.save(BookUser.of(user, book));

                user.inactive();
            }

            @Test
            @DisplayName("empty를 반환한다.")
            void it_returns_empty() {
                assertThat(bookRepository.findByBookUserEmailAndBookKey(user.getEmail(), book.getBookKey()))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("book이 존재하지 않는 경우")
        class Context_With_NoBook {

            final Book book = BookFixture.createBook();
            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                bookRepository.save(book);
                bookUserRepository.save(BookUser.of(user, book));

                book.inactive();
            }

            @Test
            @DisplayName("empty를 반환한다.")
            void it_returns_empty() {
                assertThat(bookRepository.findByBookUserEmailAndBookKey(user.getEmail(), book.getBookKey()))
                    .isEmpty();
            }
        }

        @Nested
        @DisplayName("bookUser가 존재하지 않는 경우")
        class Context_With_NoBookUser {

            final Book book = BookFixture.createBook();
            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                bookRepository.save(book);
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));

                bookUser.inactive();
            }

            @Test
            @DisplayName("empty를 반환한다.")
            void it_returns_empty() {
                assertThat(bookRepository.findByBookUserEmailAndBookKey(user.getEmail(), book.getBookKey()))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findAllByUserEmail()를 실행할 때")
    class Describe_FindAllByUserEmail {

        @Nested
        @DisplayName("book에 참여한 user의 email을 입력한 경우")
        class Context_With_BookUserEmail {

            final List<Book> involvedBooks = List.of(
                BookFixture.createBookWith("AAAAAA"),
                BookFixture.createBookWith("BBBBBB")
            );

            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                for (final Book involvedBook : involvedBooks) {
                    bookRepository.save(involvedBook);
                    bookUserRepository.save(BookUser.of(user, involvedBook));
                }
            }

            @Test
            @DisplayName("참여한 book들의 목록을 반환한다.")
            void it_returns_books() {
                assertThat(bookRepository.findAllByUserEmail(user.getEmail()))
                    .hasSameElementsAs(involvedBooks);
            }
        }

        @Nested
        @DisplayName("참여한 book이 없는 user의 email을 입력한 경우")
        class Context_With_UserEmailWithNoBook {

            final List<Book> notInvolvedBooks = List.of(BookFixture.createBook());

            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                bookRepository.saveAll(notInvolvedBooks);
            }

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void it_returns_empty() {
                assertThat(bookRepository.findAllByUserEmail(user.getEmail()))
                    .isEmpty();
            }
        }
    }
}
