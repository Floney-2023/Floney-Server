package com.floney.floney.user.infrastructure;

import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.user.service.PasswordHistoryManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordHistoryManagerImpl implements PasswordHistoryManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String KEY_USER = "user:";
    private static final String KEY_PASSWORDS = ":passwords";
    private static final int MAX_HISTORY_SIZE = 5;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOperations;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void addPassword(final String password, final long userId) {
        final String encodedPassword = passwordEncoder.encode(password);
        final String key = KEY_USER + userId + KEY_PASSWORDS;
        checkHistorySize(key);
        validateCanAddPassword(password, key);

        listOperations.rightPush(key, encodedPassword);
        removeExceedingPasswords(key);
    }

    private void removeExceedingPasswords(final String key) {
        while (listOperations.size(key) > MAX_HISTORY_SIZE) {
            listOperations.leftPop(key);
        }
    }

    private void validateCanAddPassword(final String password, final String key) {
        final List<String> history = listOperations.range(key, 0, -1);
        for (final String encodedPassword : history) {
            if (passwordEncoder.matches(password, encodedPassword)) {
                throw new PasswordSameException();
            }
        }
    }

    private void checkHistorySize(final String key) {
        if (listOperations.size(key) > MAX_HISTORY_SIZE) {
            logger.error("이전 비밀번호 내역이 {}개를 초과", MAX_HISTORY_SIZE);
        }
    }
}
