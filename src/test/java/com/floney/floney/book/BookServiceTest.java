package com.floney.floney.book;

import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.BookServiceImpl;
import com.floney.floney.common.exception.book.LimitRequestException;
import com.floney.floney.config.UserFixture;
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

import static com.floney.floney.book.BookFixture.*;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookUserRepository bookUserRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    private final CustomUserDetails userDetails;

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

        assertThat(bookService.joinWithCode(new CustomUserDetails(testUser, null), codeJoinRequest()).getCode())
                .isEqualTo(bookResponse().getCode());
    }

    @Test
    @DisplayName("구독을 했다면, 참여한 가계부가 2이하일 시 가계부를 만든다")
    void subscribe_book_create() {
        given(bookRepository.save(any(Book.class))).willReturn(createBookRequest().of(userDetails.getUsername()));

        int count = 1;

        assertThat(bookService.subscribeCreateBook(count, userDetails, createBookRequest()).getClass())
                .isEqualTo(CreateBookResponse.class);
    }

    @Test
    @DisplayName("구독을 했다면, 참여한 가계부가 2초과일 시 가계부를 만들 수 없다")
    void subscribe_book_create_exception() {
        int count = 3;
        assertThatThrownBy(() -> bookService.subscribeCreateBook(count, userDetails, createBookRequest()))
                .isInstanceOf(LimitRequestException.class);
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
