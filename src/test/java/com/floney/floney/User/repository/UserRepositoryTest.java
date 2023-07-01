package com.floney.floney.User.repository;

import com.floney.floney.config.TestConfig;
import com.floney.floney.config.UserFixture;
import com.floney.floney.user.entity.User;
import com.floney.floney.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.floney.floney.book.BookFixture.EMAIL;
import static com.floney.floney.common.constant.Status.ACTIVE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("status가 active인 유저만 조회하는 데 성공한다")
    void findActiveUsers_success() {
        // given
        userRepository.save(UserFixture.createUser());

        // when
        User user = userRepository.findByEmailAndStatus(EMAIL, ACTIVE).get();

        // then
        Assertions.assertThat(user.getEmail()).isEqualTo(EMAIL);
    }
}