package com.floney.floney.user.repository;

import com.floney.floney.config.TestConfig;
import com.floney.floney.user.dto.constant.SignoutType;
import com.floney.floney.user.entity.SignoutReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
public class SignoutReasonRepositoryTest {

    @Autowired
    private SignoutReasonRepository signoutReasonRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void init() {
        for (final SignoutType signoutType : SignoutType.values()) {
            final SignoutReason signoutReason = SignoutReason.builder()
                    .reasonType(signoutType)
                    .count(0L)
                    .build();
            signoutReasonRepository.save(signoutReason);
        }
    }

    @ParameterizedTest
    @EnumSource(SignoutType.class)
    @DisplayName("탈퇴 사유의 count를 하나 증가시키는 데 성공한다")
    void increaseSignoutReason_success(final SignoutType signoutType) {
        // when
        signoutReasonRepository.increaseCount(signoutType);
        entityManager.clear();

        // then
        final SignoutReason signoutReason = signoutReasonRepository.findByReasonType(signoutType).orElseThrow();
        assertThat(signoutReason.getCount()).isEqualTo(1);
    }
}
