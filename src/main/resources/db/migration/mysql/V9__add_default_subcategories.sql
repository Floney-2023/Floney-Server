CREATE TABLE IF NOT EXISTS `default_subcategory`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `parent_id`  bigint       NOT NULL,
    `name`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_parent_name_status_in_default_subcategory` (`parent_id`, `name`, `status`)
);

INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '급여');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '부수입');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '용돈');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '금융소득');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '사업소득');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '상여금');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '기타');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (1, '미분류');

INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '식비');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '카페/간식');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '교통');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '주거/통신');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '의료/건강');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '문화');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '여행/숙박');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '생활');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '패션/미용');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '육아');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '교육');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '경조사');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '기타');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (2, '미분류');

INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '이체');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '저축');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '현금');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '투자');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '보험');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '카드대금');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '대출');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '기타');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (3, '미분류');

INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (4, '현금');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (4, '체크카드');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (4, '신용카드');
INSERT INTO `default_subcategory` (`parent_id`, `name`)
VALUES (4, '은행');



