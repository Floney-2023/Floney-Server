package com.floney.floney.user.infrastructure;

import com.floney.floney.common.exception.user.PasswordSameException;
import com.floney.floney.user.service.PasswordHistoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PasswordHistoryManagerImpl implements PasswordHistoryManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final int MAX_HISTORY_SIZE = 5;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOperations;

    @Override
    public void addPassword(final String password, final String email) {
        checkHistorySize(email);
        validatePasswordInHistory(password, email);

        listOperations.rightPush(email, password);
        removeExceedingPasswords(email);
    }

    private void removeExceedingPasswords(final String email) {
        while (listOperations.size(email) > MAX_HISTORY_SIZE) {
            listOperations.leftPop(email);
        }
    }

    private void validatePasswordInHistory(final String password, final String email) {
        if (listOperations.indexOf(email, password) != null) {
            throw new PasswordSameException();
        }
    }

    private void checkHistorySize(final String email) {
        if (listOperations.size(email) > MAX_HISTORY_SIZE) {
            logger.error("이전 비밀번호 내역이 {}개를 초과", MAX_HISTORY_SIZE);
        }
    }
}
