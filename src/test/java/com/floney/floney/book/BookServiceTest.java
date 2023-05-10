package com.floney.floney.book;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.BookServiceImpl;
import com.floney.floney.config.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import com.querydsl.core.types.dsl.Expressions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.floney.floney.book.BookFixture.CODE;
import static com.floney.floney.book.BookFixture.EMAIL;
import static org.mockito.BDDMockito.given;

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

    @Test
    @DisplayName("초대코드로 가계부에 가입한다")
    void create_book() {
        String code = CODE.toString();
        Book testBook = BookFixture.createBookWith("1234");

        User testUser = UserFixture.createUser();
        given(bookRepository.findBookByCode(CODE))
            .willReturn(Optional.ofNullable(testBook));

        given(userRepository.findByEmail(EMAIL))
            .willReturn(Optional.ofNullable(testUser));

        given(bookUserRepository.existBookUser(EMAIL, CODE))
            .willReturn(Expressions.asBoolean(true).isTrue());

        Assertions.assertThat(bookService.joinWithCode(EMAIL, code).getCode())
            .isEqualTo(BookFixture.bookResponse().getCode());
    }

}
