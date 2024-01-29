-- 기존 카테고리 관련 테이블 복사
RENAME TABLE `category` TO `old_category`;
RENAME TABLE `book_line_category` TO `old_book_line_category`;


-- 새 카테고리 관련 테이블 생성
CREATE TABLE IF NOT EXISTS `category`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `name`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_name_status_in_category` (`name`, `status`)
);

CREATE TABLE IF NOT EXISTS `subcategory`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `parent_id`  bigint       NOT NULL,
    `book_id`    bigint       NOT NULL,
    `name`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_name_book_parent_status_in_subcategory` (`name`, `book_id`, `parent_id`, `status`)
);

CREATE TABLE IF NOT EXISTS `book_line_category`
(
    `id`                   bigint       NOT NULL AUTO_INCREMENT,
    `created_at`           datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `status`               varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`           datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `book_line_id`         bigint       NOT NULL,
    `line_category_id`     bigint       NOT NULL,
    `line_subcategory_id`  bigint       NOT NULL,
    `asset_subcategory_id` bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `book_line_in_book_line_category` (`book_line_id`),
    KEY `line_category_in_book_line_category` (`line_category_id`),
    KEY `line_subcategory_in_book_line_category` (`line_subcategory_id`),
    KEY `asset_subcategory_in_book_line_category` (`asset_subcategory_id`)
);

