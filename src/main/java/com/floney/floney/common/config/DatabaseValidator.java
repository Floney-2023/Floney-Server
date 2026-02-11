package com.floney.floney.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 프로덕션 데이터베이스(floney_run) 연결을 차단하는 검증 컴포넌트
 * 애플리케이션 시작 시 자동으로 실행되어 안전장치 역할을 합니다.
 */
@Slf4j
@Component
public class DatabaseValidator implements ApplicationRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public void run(ApplicationArguments args) {
        log.info("데이터베이스 연결 검증 중: {}", datasourceUrl);

        if (datasourceUrl.contains("floney_run")) {
            String errorMessage = "\n" +
                "═══════════════════════════════════════════════════════════\n" +
                "❌ 프로덕션 데이터베이스 연결이 차단되었습니다!\n" +
                "═══════════════════════════════════════════════════════════\n" +
                "floney_run 데이터베이스는 프로덕션 환경 전용입니다.\n" +
                "로컬 개발 환경에서는 floney_dev를 사용해주세요.\n" +
                "═══════════════════════════════════════════════════════════\n";

            log.error(errorMessage);
            throw new IllegalStateException("❌ floney_run 데이터베이스 사용이 금지되었습니다.");
        }

        log.info("✅ 데이터베이스 연결 검증 완료");
    }
}
