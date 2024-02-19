package com.floney.floney.book;

import com.floney.floney.book.domain.BookCapacity;
import com.floney.floney.book.domain.BookUserCapacity;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.BookServiceImpl;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.LimitRequestException;
import com.floney.floney.common.exception.book.MaxMemberException;
import com.floney.floney.fixture.BookFixture;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.dto.security.CustomUserDetails;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.fixture.BookFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookUserRepository bookUserRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("초대코드로 가계부에 가입한다")
    void create_book() {
        Book testBook = BookFixture.createBookWith("1234");
        User testUser = UserFixture.emailUser();

        given(bookRepository.findBookExclusivelyByCodeAndStatus(CODE, ACTIVE))
            .willReturn(Optional.ofNullable(testBook));

        given(bookUserRepository.findBookUserByCode(testUser.getEmail(), CODE))
            .willReturn(Optional.empty());

        given(bookUserRepository.countByBookExclusively(any(Book.class))).willReturn(1);

        assertThat(bookService.joinWithCode(CustomUserDetails.of(testUser), codeJoinRequest()).getCode())
            .isEqualTo(bookResponse().getCode());
    }

    @Test
    @DisplayName("참여한 가계부가 2개 이상이면 가계부를 만들 수 없다")
    void default_book_create_exception() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(2);

        assertThatThrownBy(() -> bookService.createBook(UserFixture.emailUser(), createBookRequest()))
            .isInstanceOf(LimitRequestException.class);
    }

    @Test
    @DisplayName("참여한 가계부가 2개 미만이면 가계부를 만든다")
    void default_book_create() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(1);
        given(bookRepository.save(any(Book.class)))
            .willReturn(BookFixture.createBook());

        Assertions.assertThat(bookService.createBook(UserFixture.emailUser(), createBookRequest()).getClass())
            .isEqualTo(CreateBookResponse.class);
    }

    @Test
    @DisplayName("참여한 가계부가 2개 이상이면, 가계부에 더 이상 참여할 수 없다")
    void default_book_join_limitRequestException() {
        // given
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(BookCapacity.DEFAULT.getValue());

        given(bookRepository.findBookExclusivelyByCodeAndStatus(any(String.class), any(Status.class)))
            .willReturn(Optional.ofNullable(createBook()));

        CustomUserDetails customUserDetails = CustomUserDetails.of(UserFixture.emailUser());

        // when & then
        assertThatThrownBy(() -> bookService.joinWithCode(customUserDetails, codeJoinRequest()))
            .isInstanceOf(LimitRequestException.class);
    }

    @Test
    @DisplayName("참여할 가계부의 정원이 이미 찼다면, 가계부에 더 이상 참여할 수 없다")
    void default_book_join_maxMemberException() {
        // given
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(0);

        given(bookRepository.findBookExclusivelyByCodeAndStatus(any(String.class), any(Status.class)))
            .willReturn(Optional.ofNullable(createBook()));

        given(bookUserRepository.countByBookExclusively(any(Book.class)))
            .willReturn(BookUserCapacity.DEFAULT.getValue());

        CustomUserDetails customUserDetails = CustomUserDetails.of(UserFixture.emailUser());

        // when & then
        assertThatThrownBy(() -> bookService.joinWithCode(customUserDetails, codeJoinRequest()))
            .isInstanceOf(MaxMemberException.class);
    }

    @Test
    @DisplayName("가계부 이름 변경을 요청한다")
    void change_name() {
        String changeTo = "newName";
        Book book = BookFixture.createBook();
        book.updateName(changeTo);
        Assertions.assertThat(book.getName()).isEqualTo(changeTo);
    }
}
