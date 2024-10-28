CREATE TABLE apple_subscribe (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- BaseEntity로부터 상속된 기본 키
                                user_id BIGINT,  -- User와의 OneToOne 관계로 외래 키 설정
                                transaction_id VARCHAR(255),
                                expires_date BIGINT,
                                purchase_date BIGINT,
                                notification_type VARCHAR(255),
                                signed_date BIGINT,
                                currency VARCHAR(255),
                                original_transaction_id VARCHAR(255),
                                revocation_reason INT,
                                transaction_reason VARCHAR(255),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- BaseEntity의 생성 시간
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `status`                   varchar(255) NOT NULL DEFAULT 'ACTIVE',-- BaseEntity의 수정 시간
                                KEY `fk_idx_user` (`user_id`)-- 외래 키 제약 조건 설정
);