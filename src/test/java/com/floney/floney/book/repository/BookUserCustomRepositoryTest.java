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

import static org.assertj.core.api.Assertions.assertThat;

@QueryDslTest
@DisplayName("단위 테스트: BookCustomRepository")
public class BookUserCustomRepositoryTest {

    @Autowired
    private BookUserRepository bookUserRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("findAllUser()를 실행할 때")
    class Describe_FindAllUser {

        @Nested
        @DisplayName("bookKey에 해당하는 book이 존재하는 경우")
        class Context_With_ValidBookKey {

            final String bookKey = "AAAAAA";
            final int bookUserCount = 3;

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                for (int count = 0; count < bookUserCount; count++) {
                    final User user = userRepository.save(UserFixture.emailUserWithEmail("test" + count + "@test.com"));
                    bookUserRepository.save(BookUser.of(user, book));
                }
            }

            @Test
            @DisplayName("OurBookUser 리스트를 반환한다.")
            void it_returns_ourBookUsers() {
                assertThat(bookUserRepository.findAllUser(bookKey))
                    .hasSize(bookUserCount);
            }
        }

        @Nested
        @DisplayName("bookKey에 해당하는 book이 존재하지 않는 경우")
        class Context_With_InvalidBookKey {

            final String bookKey = "AAAAAA";

            @BeforeEach
            void init() {
                final Book book = bookRepository.save(BookFixture.createBookWith("BBBBBB"));
                final User user = userRepository.save(UserFixture.emailUser());
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void it_returns_ourBookUsers() {
                assertThat(bookUserRepository.findAllUser(bookKey))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findOldestBookUserEmailExceptOwner()를 실행할 때")
    class Describe_FindOldestBookUserEmailExceptOwner {

        @Nested
        @DisplayName("book에 여러 가계부원이 참여한 경우")
        class Context_With_BookWithMultipleBookUser {

            final User owner = UserFixture.emailUserWithEmail("owner@email.com");
            final Book book = BookFixture.createBookWithOwner(owner.getEmail());

            @BeforeEach
            void init() {
                userRepository.save(owner);
                bookRepository.save(book);
                bookUserRepository.save(BookUser.of(owner, book));

                final User user1 = userRepository.save(UserFixture.emailUserWithEmail("test1@email.com"));
                bookUserRepository.save(BookUser.of(user1, book));
                final User user2 = userRepository.save(UserFixture.emailUserWithEmail("test2@email.com"));
                bookUserRepository.save(BookUser.of(user2, book));
            }

            @Test
            @DisplayName("방장을 제외하고 가장 오래된 가계부원의 이메일을 반환한다.")
            void it_returns_string() {
                assertThat(bookUserRepository.findOldestBookUserEmailExceptOwner(owner, book))
                    .hasValue("test1@email.com");
            }
        }

        @Nested
        @DisplayName("book에 방장만 참여한 경우")
        class Context_With_BookWithOnlyOwner {

            final User owner = UserFixture.emailUserWithEmail("owner@email.com");
            final Book book = BookFixture.createBookWithOwner(owner.getEmail());

            @BeforeEach
            void init() {
                userRepository.save(owner);
                bookRepository.save(book);
                bookUserRepository.save(BookUser.of(owner, book));
            }

            @Test
            @DisplayName("아무것도 반환하지 않는다.")
            void it_returns_empty() {
                assertThat(bookUserRepository.findOldestBookUserEmailExceptOwner(owner, book))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findBookUserByEmailAndBookKey()를 실행할 때")
    class Describe_FindBookUserByEmailAndBookKey {

        @Nested
        @DisplayName("email에 해당하는 bookUser가 존재하는 경우")
        class Context_With_BookUserWithEmailAndBookKey {

            final String email = "test@email.com";
            final String bookKey = "AAAAAA";

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("bookUser를 반환한다.")
            void it_returns_bookUser() {
                assertThat(bookUserRepository.findBookUserByEmailAndBookKey(email, bookKey))
                    .hasValueSatisfying(bookUser -> {
                        assertThat(bookUser.getUser().getEmail()).isEqualTo(email);
                        assertThat(bookUser.getBook().getBookKey()).isEqualTo(bookKey);
                    });
            }
        }

        @Nested
        @DisplayName("유저가 해당 가계부에 존재하지 않는 경우")
        class Context_With_BookUserNotExistsInBook {

            final String email = "test@email.com";
            final String bookKey = "AAAAAA";

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                final Book book = bookRepository.save(BookFixture.createBook());
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("아무것도 반환하지 않는다.")
            void it_returns_empty() {
                assertThat(bookUserRepository.findBookUserByEmailAndBookKey(email, bookKey))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("existsByBookKeyAndUserEmail()를 실행할 때")
    class Describe_ExistsByBookKeyAndUserEmail {

        @Nested
        @DisplayName("email에 해당하는 bookUser가 존재하는 경우")
        class Context_With_BookUserWithEmailAndBookKey {

            final String email = "test@email.com";
            final String bookKey = "AAAAAA";

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                final Book book = bookRepository.save(BookFixture.createBookWith(bookKey));
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("true를 반환한다.")
            void it_returns_true() {
                assertThat(bookUserRepository.existsByBookKeyAndUserEmail(bookKey, email))
                    .isTrue();
            }
        }

        @Nested
        @DisplayName("유저가 해당 가계부에 존재하지 않는 경우")
        class Context_With_BookUserNotExistsInBook {

            final String email = "test@email.com";
            final String bookKey = "AAAAAA";

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                final Book book = bookRepository.save(BookFixture.createBook());
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("false를 반환한다.")
            void it_returns_false() {
                assertThat(bookUserRepository.existsByBookKeyAndUserEmail(bookKey, email))
                    .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("findBookUserByCode()를 실행할 때")
    class Describe_FindBookUserByCode {

        @Nested
        @DisplayName("해당 가계부에 bookUser가 존재하는 경우")
        class Context_With_BookUserWithEmailAndBookCode {

            final String email = "test@email.com";
            final String bookCode = "AAAAAA";

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                final Book book = bookRepository.save(BookFixture.createBookWithCode(bookCode));
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("bookUser를 반환한다.")
            void it_returns_bookUser() {
                assertThat(bookUserRepository.findBookUserByCode(email, bookCode))
                    .hasValueSatisfying(bookUser -> {
                        assertThat(bookUser.getUser().getEmail()).isEqualTo(email);
                        assertThat(bookUser.getBook().getCode()).isEqualTo(bookCode);
                    });
            }
        }

        @Nested
        @DisplayName("다른 가계부에 bookUser가 존재하는 경우")
        class Context_With_BookUserWithEmailAndDifferentBookCode {

            final String email = "test@email.com";
            final String bookCode = "AAAAAA";

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUserWithEmail(email));
                final Book book = bookRepository.save(BookFixture.createBookWithCode("BBBBBB"));
                bookUserRepository.save(BookUser.of(user, book));
            }

            @Test
            @DisplayName("아무것도 반환하지 않는다.")
            void it_returns_empty() {
                assertThat(bookUserRepository.findBookUserByCode(email, bookCode))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findMyBookInfos()를 실행할 때")
    class Describe_FindMyBookInfos {

        @Nested
        @DisplayName("유저가 참여한 가계부가 존재하는 경우")
        class Context_With_BookUserExists {

            final User user = UserFixture.emailUser();
            final int bookCount = 3;

            @BeforeEach
            void init() {
                userRepository.save(user);
                for (int count = 0; count < bookCount; count++) {
                    final Book book = bookRepository.save(BookFixture.createBookWith("AAAAA" + count));
                    bookUserRepository.save(BookUser.of(user, book));
                }
            }

            @Test
            @DisplayName("MyBookInfo 리스트를 반환한다.")
            void it_returns_myBookInfos() {
                assertThat(bookUserRepository.findMyBookInfos(user))
                    .hasSize(bookCount)
                    .extracting("memberCount", Long.class)
                    .allSatisfy(memberCount -> assertThat(memberCount).isEqualTo(1));
            }
        }

        @Nested
        @DisplayName("유저가 참여한 가계부가 존재하지 않는 경우")
        class Context_With_OtherBookUser {

            final User user = UserFixture.emailUser();

            @BeforeEach
            void init() {
                userRepository.save(user);
                final User otherUser = userRepository.save(UserFixture.emailUserWithEmail("other@email.com"));
                final Book book = bookRepository.save(BookFixture.createBook());
                bookUserRepository.save(BookUser.of(otherUser, book));
            }

            @Test
            @DisplayName("빈 목록를 반환한다.")
            void it_returns_empty() {
                assertThat(bookUserRepository.findMyBookInfos(user))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("countByBook()를 실행할 때")
    class Describe_CountByBook {

        @Nested
        @DisplayName("book에 참여한 bookUser가 존재하는 경우")
        class Context_With_BookUser {

            final Book book = BookFixture.createBook();
            final int bookUserCount = 3;

            @BeforeEach
            void init() {
                bookRepository.save(book);

                for (int count = 0; count < bookUserCount; count++) {
                    final User user = userRepository.save(UserFixture.emailUserWithEmail("test" + count + "@test.com"));
                    bookUserRepository.save(BookUser.of(user, book));
                }
            }

            @Test
            @DisplayName("bookUser의 수를 반환한다.")
            void it_returns_int() {
                assertThat(bookUserRepository.countByBook(book))
                    .isEqualTo(bookUserCount);
            }
        }

        @Nested
        @DisplayName("book에 참여한 bookUser가 존재하지 않는 경우")
        class Context_With_NoBookUser {

            final Book book = BookFixture.createBook();

            @BeforeEach
            void init() {
                bookRepository.save(book);

                final Book otherBook = bookRepository.save(BookFixture.createBookWith("AAAAAA"));
                final User user = userRepository.save(UserFixture.emailUser());
                bookUserRepository.save(BookUser.of(user, otherBook));
            }

            @Test
            @DisplayName("bookUser의 수를 반환한다.")
            void it_returns_int() {
                assertThat(bookUserRepository.countByBook(book))
                    .isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("countByBookExclusively()를 실행할 때")
    class Describe_CountByBookExclusively {

        @Nested
        @DisplayName("book에 참여한 bookUser가 존재하는 경우")
        class Context_With_BookUser {

            final Book book = BookFixture.createBook();
            final int bookUserCount = 3;

            @BeforeEach
            void init() {
                bookRepository.save(book);

                for (int count = 0; count < bookUserCount; count++) {
                    final User user = userRepository.save(UserFixture.emailUserWithEmail("test" + count + "@test.com"));
                    bookUserRepository.save(BookUser.of(user, book));
                }
            }

            @Test
            @DisplayName("bookUser의 수를 반환한다.")
            void it_returns_int() {
                assertThat(bookUserRepository.countByBookExclusively(book))
                    .isEqualTo(bookUserCount);
            }
        }

        @Nested
        @DisplayName("book에 참여한 bookUser가 존재하지 않는 경우")
        class Context_With_NoBookUser {

            final Book book = BookFixture.createBook();

            @BeforeEach
            void init() {
                bookRepository.save(book);

                final Book otherBook = bookRepository.save(BookFixture.createBookWith("AAAAAA"));
                final User user = userRepository.save(UserFixture.emailUser());
                bookUserRepository.save(BookUser.of(user, otherBook));
            }

            @Test
            @DisplayName("bookUser의 수를 반환한다.")
            void it_returns_int() {
                assertThat(bookUserRepository.countByBookExclusively(book))
                    .isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("findAllByUserId()를 실행할 때")
    class Describe_FindAllByUserId {

        @Nested
        @DisplayName("userId에 해당하는 bookUser가 존재하는 경우")
        class Context_With_BookUserByUserId {

            long userId;
            final int bookCount = 3;

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUser());
                userId = user.getId();

                for (int count = 0; count < bookCount; count++) {
                    final Book book = bookRepository.save(BookFixture.createBookWith("AAAAA" + count));
                    bookUserRepository.save(BookUser.of(user, book));
                }
            }

            @Test
            @DisplayName("bookUser 리스트를 반환한다.")
            void it_returns_bookUsers() {
                assertThat(bookUserRepository.findAllByUserId(userId))
                    .hasSize(bookCount);
            }
        }

        @Nested
        @DisplayName("userId에 해당하는 bookUser가 존재하지 않는 경우")
        class Context_With_NoBookUser {

            long userId;

            @BeforeEach
            void init() {
                final User user = userRepository.save(UserFixture.emailUser());
                userId = user.getId();

                bookRepository.save(BookFixture.createBook());
            }

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void it_returns_empty() {
                assertThat(bookUserRepository.findAllByUserId(userId))
                    .isEmpty();
            }
        }
    }
}
