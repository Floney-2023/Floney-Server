package com.floney.floney.book.service;

import com.floney.floney.book.domain.BookCapacity;
import com.floney.floney.book.domain.BookUserCapacity;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.entity.RepeatBookLine;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.RepeatBookLineRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.repository.category.DefaultSubcategoryRepository;
import com.floney.floney.book.repository.category.SubcategoryRepository;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.LimitRequestException;
import com.floney.floney.common.exception.book.MaxMemberException;
import com.floney.floney.fixture.*;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.RepeatDuration.MONTH;
import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.fixture.BookFixture.*;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@DisplayName("단위 테스트 : BookService")
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookUserRepository bookUserRepository;

    @Mock
    private BookLineRepository bookLineRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DefaultSubcategoryRepository defaultSubcategoryRepository;

    @Mock
    private SubcategoryRepository subcategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RepeatBookLineRepository repeatBookLineRepository;


    @Nested
    @DisplayName("joinWithCode()를 실행할 때")
    class Describe_JoinWithCode {

        @Nested
        @DisplayName("초대코드가 유효한다면")
        class Context_With_ValidCode {

            User testUser;
            Book testBook;

            @BeforeEach
            void init() {
                testBook = BookFixture.createBookWith("1234");
                testUser = UserFixture.emailUser();

                given(bookRepository.findBookExclusivelyByCodeAndStatus(anyString(), any(Status.class)))
                    .willReturn(Optional.of(testBook));

                given(bookUserRepository.findBookUserByCode(anyString(), anyString()))
                    .willReturn(Optional.empty());

                given(bookUserRepository.countByBookExclusively(any(Book.class))).willReturn(1);

            }

            @Test
            @DisplayName("기계부 참여에 성공한다")
            void it_return_code() {
                assertThat(bookService.joinWithCode("android",CustomUserDetails.of(testUser), codeJoinRequest()).getCode())
                    .isEqualTo(testBook.getCode());
            }
        }
    }

    @Nested
    @DisplayName("GetAllRepeatBookLine()을 실행할 때")
    class Context_GetAllRepeatBookLine {


        @Nested
        @DisplayName("가계부 내역이 존재하는 반복 내역이 주어지는 경우")
        class Describe_ExistBookLine {

            Book book;
            RepeatBookLine repeatBookLine;

            @BeforeEach
            void init() {
                book = createBook();
                BookUser bookUser = BookUserFixture.createBookUser(book, UserFixture.emailUser());
                repeatBookLine = RepeatBookLineFixture.repeatBookLine(CategoryFixture.create(INCOME), bookUser, MONTH);
                ReflectionTestUtils.setField(repeatBookLine, "id", 1L);

                given(bookRepository.findBookByBookKeyAndStatus(anyString(), any(Status.class)))
                    .willReturn(ofNullable(book));
                given(categoryRepository.findByType(any(CategoryType.class)))
                    .willReturn(ofNullable(CategoryFixture.create(INCOME)));
                given(repeatBookLineRepository.findAllByBookAndStatusAndLineCategory(any(Book.class), any(Status.class), any(Category.class)))
                    .willReturn(Arrays.asList(repeatBookLine));
                given(bookLineRepository.existsBookLineByStatusAndRepeatBookLine(ACTIVE, repeatBookLine))
                    .willReturn(true);

            }

            @Test
            @DisplayName("해당 반복 내역은 조회 된다")
            void it_return_repeatBookLine() {
                Assertions.assertThat(bookService.getAllRepeatBookLine(book.getBookKey(), INCOME)).isNotEmpty();
                Assertions.assertThat(repeatBookLine.isActive()).isTrue();


            }
        }

        @Nested
        @DisplayName("가계부 내역이 존재하지 않는 반복 내역이 주어지는 경우")
        class Describe_NotExistBookLine {

            Book book;
            RepeatBookLine repeatBookLine;

            @BeforeEach
            void init() {
                book = createBook();
                BookUser bookUser = BookUserFixture.createBookUser(book, UserFixture.emailUser());
                repeatBookLine = RepeatBookLineFixture.repeatBookLine(CategoryFixture.create(INCOME), bookUser, MONTH);
                ReflectionTestUtils.setField(repeatBookLine, "id", 1L);

                given(bookRepository.findBookByBookKeyAndStatus(anyString(), any(Status.class)))
                    .willReturn(ofNullable(book));
                given(categoryRepository.findByType(any(CategoryType.class)))
                    .willReturn(ofNullable(CategoryFixture.create(INCOME)));
                given(repeatBookLineRepository.findAllByBookAndStatusAndLineCategory(any(Book.class), any(Status.class), any(Category.class)))
                    .willReturn(Arrays.asList(repeatBookLine));
                given(bookLineRepository.existsBookLineByStatusAndRepeatBookLine(ACTIVE, repeatBookLine)).willReturn(false);

            }

            @Test
            @DisplayName("해당 반복 내역은 비활성화 되며 조회 되지 않는다")
            void it_inactive_repeatLine() {
                Assertions.assertThat(bookService.getAllRepeatBookLine(book.getBookKey(), INCOME)).isEmpty();
                Assertions.assertThat(repeatBookLine.isActive()).isFalse();


            }
        }
    }


    @Nested
    @DisplayName("createBook()를 실행할 때")
    class Describe_CreateBook {
        @Nested
        @DisplayName("참여한 가계부가 2개 이상이라면")
        class Context_With_OverJoin {

            CustomUserDetails customUserDetails;

            @BeforeEach
            void init() {
                // given
                given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
                    .willReturn(BookCapacity.DEFAULT.getValue());

                given(bookRepository.findBookExclusivelyByCodeAndStatus(any(String.class), any(Status.class)))
                    .willReturn(ofNullable(createBook()));

                customUserDetails = CustomUserDetails.of(UserFixture.emailUser());

            }

            @Test
            @DisplayName("에러를 반환한다")
            void it_returns_exception() {
                assertThatThrownBy(() -> bookService.joinWithCode("android",customUserDetails, codeJoinRequest()))
                    .isInstanceOf(LimitRequestException.class);

            }
        }

        @Nested
        @DisplayName("참여한 가계부가 2개 미만이라면")
        class Context_With_ValidJoin {
            Book book;

            @BeforeEach
            void init() {
                book = BookFixture.createBookWith("book-key");
                given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
                    .willReturn(1);
                given(bookRepository.save(any(Book.class)))
                    .willReturn(book);
                given(defaultSubcategoryRepository.findAllByStatus(ACTIVE))
                    .willReturn(List.of());

            }

            @Test
            @DisplayName("성공한다")
            void it_returns_success() {
                assertThatNoException().isThrownBy(() -> bookService.createBook(UserFixture.emailUser(), createBookRequest()));
            }
        }

        @Nested
        @DisplayName("참여할 가계부의 정원이 이미 찼다면")
        class Context_With_FullJoin {
            CustomUserDetails customUserDetails;

            @BeforeEach()
            void init() {
                given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
                    .willReturn(0);

                given(bookRepository.findBookExclusivelyByCodeAndStatus(any(String.class), any(Status.class)))
                    .willReturn(ofNullable(createBook()));

                given(bookUserRepository.countByBookExclusively(any(Book.class)))
                    .willReturn(BookUserCapacity.DEFAULT.getValue());

                customUserDetails = CustomUserDetails.of(UserFixture.emailUser());
            }

            @Test
            @DisplayName("예외를 반환한다")
            void it_returns_exception() {
                assertThatThrownBy(() -> bookService.joinWithCode("android",customUserDetails, codeJoinRequest()))
                    .isInstanceOf(MaxMemberException.class);
            }
        }
    }

}
