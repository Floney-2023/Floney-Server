package com.floney.floney.user.repository;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static com.floney.floney.fixture.BookFixture.EMAIL;

import com.floney.floney.config.TestConfig;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("status가 active인 유저만 조회하는 데 성공한다")
    void findActiveUsers_success() {
        // given
        userRepository.save(UserFixture.createUser());

        // when
        User user = userRepository.findByEmailAndStatus(EMAIL, ACTIVE).orElseThrow();

        // then
        Assertions.assertThat(user.getEmail()).isEqualTo(EMAIL);
    }
}
