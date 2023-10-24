package com.floney.floney.book;

import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.BookServiceImpl;
import com.floney.floney.common.constant.Status;
import com.floney.floney.common.exception.book.LimitRequestException;
import com.floney.floney.common.exception.book.MaxMemberException;
import com.floney.floney.common.exception.common.NotSubscribeException;
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

    private final CustomUserDetails userDetails;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookUserRepository bookUserRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    public BookServiceTest() {
        this.userDetails = new CustomUserDetails(UserFixture.getUser(), null);
    }

    @Test
    @DisplayName("초대코드로 가계부에 가입한다")
    void create_book() {
        Book testBook = BookFixture.createBookWith("1234");
        User testUser = UserFixture.getUser();

        given(bookRepository.findBookByCodeAndStatus(CODE, ACTIVE))
            .willReturn(Optional.ofNullable(testBook));

        given(bookUserRepository.findBookUserByCode(testUser.getEmail(), CODE))
            .willReturn(Optional.empty());

        given(bookUserRepository.getCurrentJoinUserCountExclusively(any(Book.class))).willReturn(1);

        assertThat(bookService.joinWithCode(CustomUserDetails.of(testUser), codeJoinRequest()).getCode())
            .isEqualTo(bookResponse().getCode());
    }

    @Test
    @DisplayName("가계부 생성시 구독을 했다면, 참여한 가계부가 2이하일 시 가계부를 만든다")
    void subscribe_book_create() {
        given(bookRepository.save(any(Book.class))).willReturn(createBookRequest().of(userDetails.getUsername()));

        assertThat(bookService.subscribeCreateBook(userDetails.getUser(), createBookRequest()).getClass())
            .isEqualTo(CreateBookResponse.class);
    }

    @Test
    @DisplayName("가계부 생성시 구독을 했다면 , 유저가 참여한 가계부가 2 초과일 시 제공하지 않는 서비스 예외 발생")
    void subscribe_book_create_exception() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(3);

        assertThatThrownBy(() -> bookService.addBook(UserFixture.createSubscribeUser(), createBookRequest()))
            .isInstanceOf(LimitRequestException.class);
    }

    @Test
    @DisplayName("가계부 생성시 구독을 안했다면, 참여한 가계부가 1 초과일 시 가계부를 만들 수 없다")
    void default_book_create_exception() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(2);

        assertThatThrownBy(() -> bookService.addBook(UserFixture.createUser(), createBookRequest()))
            .isInstanceOf(NotSubscribeException.class);
    }

    @Test
    @DisplayName("가계부 생성시 구독을 안했다면, 참여한 가계부가 1미만 일 시 가계부를 만든다")
    void default_book_create() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(0);
        given(bookRepository.save(any(Book.class)))
            .willReturn(BookFixture.createBook());

        Assertions.assertThat(bookService.addBook(UserFixture.createUser(), createBookRequest()).getClass())
            .isEqualTo(CreateBookResponse.class);
    }

    @Test
    @DisplayName("가계부 참여 시 구독을 안했다면, 유저가 현재 참여한 가계부가 2 초과일시, 구독 제한 예외가 터진다")
    void default_book_join_exception() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(3);

        given(bookRepository.findBookByCodeAndStatus(any(String.class), any(Status.class)))
            .willReturn(Optional.ofNullable(createBook()));

        CustomUserDetails customUserDetails = CustomUserDetails.of(UserFixture.createUser());

        assertThatThrownBy(() -> bookService.joinWithCode(customUserDetails, codeJoinRequest()))
            .isInstanceOf(NotSubscribeException.class);
    }

    @Test
    @DisplayName("가계부 참여 시 참여 하려는 가계부의 정원 초과 시 가계부 정원 초과 예외 발생")
    void default_book_join() {
        given(bookUserRepository.countBookUserByUserAndStatus(any(User.class), any(ACTIVE.getClass())))
            .willReturn(0);

        // 가계부 정원이 2인 가계부 생성
        given(bookRepository.findBookByCodeAndStatus(any(String.class), any(Status.class)))
            .willReturn(Optional.ofNullable(createBook()));

        given(bookUserRepository.getCurrentJoinUserCountExclusively(any(Book.class))).willReturn(4);

        CustomUserDetails customUserDetails = CustomUserDetails.of(UserFixture.createUser());

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
