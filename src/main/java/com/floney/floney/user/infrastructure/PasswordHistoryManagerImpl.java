package com.floney.floney.user.infrastructure;

import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.user.service.PasswordHistoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordHistoryManagerImpl implements PasswordHistoryManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String KEY_USER = "user:";
    private static final String KEY_PASSWORDS = ":passwords";
    private static final int MAX_HISTORY_SIZE = 5;
    private static final int START_INDEX = 0;
    private static final int END_INDEX = -1;

    private final RedisTemplate<String, String> redisTemplate;
    private final ListOperations<String, String> listOperations;
    private final PasswordEncoder passwordEncoder;

    private PasswordHistoryManagerImpl(final RedisTemplate<String, String> redisTemplate,
                                       final PasswordEncoder passwordEncoder) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void addPassword(final String password, final long userId) {
        final String encodedPassword = passwordEncoder.encode(password);
        final String key = KEY_USER + userId + KEY_PASSWORDS;
        checkHistorySize(key);
        validateCanAddPassword(password, key);

        listOperations.rightPush(key, encodedPassword);
        removeExceedingPasswords(key);
    }

    @Override
    public void deleteHistory(final long userId) {
        final String key = KEY_USER + userId + KEY_PASSWORDS;
        redisTemplate.unlink(key);
    }

    private void removeExceedingPasswords(final String key) {
        while (listOperations.size(key) > MAX_HISTORY_SIZE) {
            listOperations.leftPop(key);
        }
    }

    private void validateCanAddPassword(final String password, final String key) {
        final List<String> history = listOperations.range(key, START_INDEX, END_INDEX);
        if (isPasswordInHistory(password, history)) {
            throw new PasswordSameException();
        }
    }

    private boolean isPasswordInHistory(final String password, final List<String> history) {
        return history.stream()
            .anyMatch(encodedPassword -> passwordEncoder.matches(password, encodedPassword));
    }

    private void checkHistorySize(final String key) {
        if (listOperations.size(key) > MAX_HISTORY_SIZE) {
            logger.warn("Redis에서 이전 비밀번호 내역이 {}개를 초과", MAX_HISTORY_SIZE);
        }
    }
}
