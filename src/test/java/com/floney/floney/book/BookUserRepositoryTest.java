package com.floney.floney.book;

import com.floney.floney.book.dto.MyBookInfo;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.config.TestConfig;
import com.floney.floney.config.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

import static com.floney.floney.book.BookFixture.NAME;
import static com.floney.floney.book.BookFixture.URL;

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

    private User savedUser;
    private Book savedBook;

    @BeforeEach
    void init() {
        savedUser = userRepository.save(UserFixture.createUser());
        savedBook = bookRepository.save(BookFixture.createBook());
    }

    @Test
    @DisplayName("가계부 코드와 닉네임을 동시에 만족하는 BookUser를 조회한다")
    void getBookUser() {
        BookUser newBookUser = BookUser.builder()
            .book(savedBook)
            .user(savedUser)
            .build();

        bookUserRepository.save(newBookUser);
        BookUser find = bookUserRepository.findUserWith(savedUser.getNickname(), savedBook.getBookKey());
        Assertions.assertThat(find.getBook().getName())
            .isEqualTo(savedBook.getName());
    }

    @Test
    @DisplayName("사용자가 참여하는 가계부의 정보를 가져온다")
    void getMyBooks() {
        Book savedBook2 = bookRepository.save(BookFixture.createBook());

        BookUser newBookUser = BookUser.builder()
            .book(savedBook)
            .user(savedUser)
            .build();

        BookUser newBookUser2 = BookUser.builder()
            .book(savedBook2)
            .user(savedUser)
            .build();

        bookUserRepository.save(newBookUser);
        bookUserRepository.save(newBookUser2);

        MyBookInfo myInfo = MyBookInfo.builder()
            .name(NAME)
            .bookImg(URL)
            .memberCount(1L)
            .build();

        Assertions.assertThat(bookUserRepository.findMyBooks(savedUser))
            .isEqualTo(Arrays.asList(myInfo,myInfo));
    }

}
