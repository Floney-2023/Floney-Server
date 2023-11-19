package com.floney.floney.book;

import com.floney.floney.book.dto.response.CreateBookResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.BookServiceImpl;
import com.floney.floney.common.constant.Status;
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
        User testUser = UserFixture.getUser();

        given(bookRepository.findBookExclusivelyByCodeAndStatus(CODE, ACTIVE))
                .willReturn(Optional.ofNullable(testBook));

        given(bookUserRepository.findBookUserByCode(testUser.getEmail(), CODE))
                .willReturn(Optional.empty());

        given(bookUserRepository.countByBookExclusively(any(Book.class))).willReturn(1);

        assertThat(bookService.joinWithCode(CustomUserDetails.of(testUser), codeJoinRequest()).getCode())
                .isEqualTo(bookResponse().getCode());
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
