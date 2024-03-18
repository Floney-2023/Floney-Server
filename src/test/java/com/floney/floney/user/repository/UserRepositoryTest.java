package com.floney.floney.user.repository;

import com.floney.floney.config.QueryDslTest;
import com.floney.floney.fixture.UserFixture;
import com.floney.floney.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.floney.floney.common.constant.Status.ACTIVE;

@QueryDslTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("status가 active인 유저만 조회하는 데 성공한다")
    void findActiveUsers_success() {
        // given
        userRepository.save(UserFixture.emailUser());

        // when
        User user = userRepository.findByEmailAndStatus(UserFixture.DEFAULT_EMAIL, ACTIVE).orElseThrow();

        // then
        Assertions.assertThat(user.getEmail()).isEqualTo(UserFixture.DEFAULT_EMAIL);
    }
}
