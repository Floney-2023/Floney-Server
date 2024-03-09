package com.floney.floney.book.repository;


import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.RepeatBookLineFixture;
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
@DisplayName("단위 테스트: RepeatBookLineRepositoryTest")
public class RepeatBookLineRepositoryTest {

    @Autowired
    private RepeatBookLineRepository repeatBookLineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookUserRepository bookUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Nested
    @DisplayName("inactiveAllBy()를 실행할 때")
    class Describe_InactiveAllBy {

        @Nested
        @DisplayName("반복내역이 존재하는 경우")
        class Context_With_RepeatBookLine {

            Book book;
            Category category;

            @BeforeEach
            void init() {
                String bookKey = "1234";
                book = bookRepository.save(BookFixture.createBookWith(bookKey));

                final User user = userRepository.save(UserFixture.emailUser());
                final BookUser bookUser = bookUserRepository.save(BookUser.of(user, book));
                category = categoryRepository.findByType(CategoryType.INCOME).get();

                final RepeatBookLine repeatBookLine = RepeatBookLineFixture.createRepeatBookLine(category, bookUser, book, RepeatDuration.EVERYDAY);
                final RepeatBookLine repeatBookLine2 = RepeatBookLineFixture.createRepeatBookLine(category, bookUser, book, RepeatDuration.MONTH);

                repeatBookLineRepository.save(repeatBookLine);
                repeatBookLineRepository.save(repeatBookLine2);
            }

            @Test
            @DisplayName("반복 내역을 모두 비활성화 시킨다")
            void it_inactive_all() {
                repeatBookLineRepository.inactiveAllByBook(book);
                assertThat(repeatBookLineRepository.findAllByBookAndStatusAndLineCategory(book, Status.ACTIVE, category))
                    .isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("inactiveAllByBookUser()를 실행할 때")
    class Describe_InactiveAllByBookUser {

        @Nested
        @DisplayName("반복내역이 존재하는 경우")
        class Context_With_RepeatBookLine {

            Book book;
            BookUser bookUser;
            Category category;

            @BeforeEach
            void init() {
                String bookKey = "1234";
                book = bookRepository.save(BookFixture.createBookWith(bookKey));

                final User user = userRepository.save(UserFixture.emailUser());
                bookUser = bookUserRepository.save(BookUser.of(user, book));
                category = categoryRepository.findByType(CategoryType.INCOME).get();

                final RepeatBookLine repeatBookLine = RepeatBookLineFixture.createRepeatBookLine(category, bookUser, book, RepeatDuration.EVERYDAY);
                final RepeatBookLine repeatBookLine2 = RepeatBookLineFixture.createRepeatBookLine(category, bookUser, book, RepeatDuration.MONTH);

                repeatBookLineRepository.save(repeatBookLine);
                repeatBookLineRepository.save(repeatBookLine2);
            }

            @Test
            @DisplayName("반복 내역을 모두 비활성화 시킨다")
            void it_inactive_all() {
                repeatBookLineRepository.inactiveAllByBookUser(bookUser);
                assertThat(repeatBookLineRepository.findAllByBookAndStatusAndLineCategory(book, Status.ACTIVE, category))
                    .isEmpty();
            }
        }
    }
}
