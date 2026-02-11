# H2 Database 마이그레이션 가이드

## 변경 이유
개발 서버 환경(1코어 1GB)에서 MySQL 컨테이너가 차지하는 메모리 부담을 줄이기 위해 H2 Database로 전환했습니다.

### 메모리 절약 효과
- MySQL 8.0 컨테이너: 약 300-400MB
- H2 embedded: 약 10-20MB
- **절약량: 약 380MB** ✅

## 주요 변경 사항

### 1. Database 설정 (`application-dev.yaml`)
```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    username: sa
    password:
    url: jdbc:h2:file:./data/floney_dev;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

### 2. H2 설정 옵션 설명
- `MODE=MySQL`: MySQL 호환 모드 활성화
- `DATABASE_TO_LOWER=TRUE`: 테이블/컬럼명을 소문자로 처리
- `CASE_INSENSITIVE_IDENTIFIERS=TRUE`: 대소문자 구분 안함 (MySQL 기본 동작)
- `file:./data/floney_dev`: 파일 기반 DB (데이터 영속성 보장)

### 3. Docker Compose 변경
- MySQL 컨테이너 제거 (주석 처리)
- H2 데이터 파일 볼륨 마운트 추가: `./data:/app/data`
- MySQL 의존성 제거로 앱 단독 실행

### 4. Build 설정 (`build.gradle`)
```gradle
runtimeOnly 'com.h2database:h2'
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-mysql'  // MySQL migration 호환성 유지
```

## 사용 방법

### 로컬 개발 환경
```bash
# 프로파일 활성화
./gradlew bootRun --args='--spring.profiles.active=develop'
```

### Docker 환경 (개발 서버)
```bash
# 기존 MySQL 컨테이너 정리
docker-compose down -v

# 새로운 구성으로 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app
```

### H2 Console 접속
개발 환경에서 H2 콘솔에 접속하여 데이터를 확인할 수 있습니다:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/floney_dev`
- Username: `sa`
- Password: (빈 값)

## 데이터 마이그레이션

### MySQL → H2 데이터 이관 (필요시)
```bash
# 1. MySQL 데이터 덤프
docker exec floney-mysql mysqldump -u root -p1022 floney_dev > backup.sql

# 2. H2에서 사용 가능한 형식으로 수정 (필요한 경우)
# - MySQL 특화 함수 제거/수정
# - 인코딩 관련 설정 제거

# 3. H2 콘솔에서 SQL 실행
```

## Flyway Migration
기존 MySQL 마이그레이션 스크립트가 H2 MySQL 호환 모드에서 대부분 그대로 작동합니다.

### 확인된 호환 항목
- ✅ AUTO_INCREMENT
- ✅ TINYINT, BIGINT, DOUBLE
- ✅ DATETIME(6), DATE
- ✅ DEFAULT CURRENT_TIMESTAMP(6)
- ✅ UNIQUE KEY, INDEX
- ✅ 백틱(`) 사용

## 주의사항

### H2의 제한사항
1. **완벽한 MySQL 호환은 아님** (약 99% 호환)
2. **동시 접속 성능**: 개발 환경에선 문제없지만, 대량 트래픽엔 부적합
3. **프로덕션 환경**: 반드시 MySQL 사용 (application-prod.yaml)

### 프로덕션 vs 개발 차이점
- **개발(dev)**: H2 file-based DB
- **프로덕션(prod)**: MySQL 8.0
- 대부분의 SQL은 호환되지만, 특정 MySQL 함수는 테스트 필요

## 롤백 방법

H2가 문제가 있을 경우 다시 MySQL로 되돌릴 수 있습니다:

### 1. application-dev.yaml 수정
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    password: 1022
    username: root
    url: jdbc:mysql://127.0.01:3306/floney_dev?&rewriteBatchedStatements=true
```

### 2. docker-compose.yml 주석 해제
MySQL 서비스 부분의 주석을 제거하고 다시 실행

```bash
docker-compose up -d mysql
```

## 트러블슈팅

### Migration 실패 시
```bash
# Flyway 메타데이터 확인
# H2 콘솔에서 실행
SELECT * FROM flyway_schema_history;

# 필요시 특정 버전 repair
./gradlew flywayRepair
```

### 데이터 파일 위치
- 로컬: `./data/floney_dev.mv.db`
- Docker: `/app/data/floney_dev.mv.db` (호스트의 `./data`와 바인딩)

### 성능 이슈 시
H2 성능 튜닝 옵션 추가:
```
jdbc:h2:file:./data/floney_dev;MODE=MySQL;CACHE_SIZE=32768;PAGE_SIZE=8192
```

## 참고 자료
- [H2 Database Documentation](http://www.h2database.com/)
- [H2 MySQL Compatibility Mode](http://www.h2database.com/html/features.html#compatibility)
- [Flyway Documentation](https://flywaydb.org/documentation/)
