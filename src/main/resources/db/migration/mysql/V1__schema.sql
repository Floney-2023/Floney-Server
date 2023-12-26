CREATE TABLE IF NOT EXISTS `alarm`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `status`       varchar(10)  NOT NULL DEFAULT 'ACTIVE',
    `updated_at`   datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `title`        varchar(50)  NOT NULL,
    `date`         datetime(6)  NOT NULL,
    `img_url`      varchar(255)          DEFAULT NULL,
    `is_received`  tinyint      NOT NULL DEFAULT '0',
    `book_id`      bigint       NOT NULL,
    `book_user_id` bigint       NOT NULL,
    `body`         varchar(100) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_alarm` (`book_id`),
    KEY `fk_idx_book_user_in_alarm` (`book_user_id`)
);

CREATE TABLE IF NOT EXISTS `asset`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `date`       date         NOT NULL,
    `money`      double       NOT NULL,
    `book_id`    bigint       NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `book_and_date_on_asset` (`book_id`, `date`)
);

CREATE TABLE IF NOT EXISTS `book`
(
    `id`                   bigint       NOT NULL AUTO_INCREMENT,
    `created_at`           datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`               varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`           datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `book_key`             varchar(10)  NOT NULL,
    `code`                 varchar(10)  NOT NULL,
    `name`                 varchar(10)  NOT NULL,
    `book_img`             varchar(300)          DEFAULT NULL,
    `owner`                varchar(60)  NOT NULL,
    `last_settlement_date` date                  DEFAULT NULL,
    `carry_over_money`     double       NOT NULL DEFAULT '0.0',
    `currency`             varchar(10)  NOT NULL DEFAULT 'KRW',
    `user_capacity`        int          NOT NULL,
    `carry_over_status`    tinyint      NOT NULL DEFAULT '0',
    `see_profile`          tinyint      NOT NULL DEFAULT '1',
    `asset`                double       NOT NULL DEFAULT '0.0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_book_key_on_book` (`book_key`)
);

CREATE TABLE IF NOT EXISTS `book_line`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`        varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `description`   varchar(255),
    `except_status` tinyint      NOT NULL DEFAULT '0',
    `line_date`     date         NOT NULL,
    `money`         double       NOT NULL DEFAULT '0.0',
    `book_id`       bigint       NOT NULL,
    `writer_id`     bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_book_line` (`book_id`),
    KEY `fk_idx_user_in_book_line` (`writer_id`)
);

CREATE TABLE IF NOT EXISTS `book_line_category`
(
    `id`                       bigint       NOT NULL AUTO_INCREMENT,
    `created_at`               datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`                   varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`               datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `name`                     varchar(10)  NOT NULL,
    `book_line_id`             bigint       NOT NULL,
    `category_id`              bigint       NOT NULL,
    `book_line_categories_key` varchar(10)  NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_line_in_book_line_category` (`book_line_id`),
    KEY `fk_idx_category_in_book_line_category` (`category_id`)
);

CREATE TABLE IF NOT EXISTS `book_user`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`      varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`  datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `profile_img` varchar(300) NOT NULL DEFAULT 'user_default',
    `book_id`     bigint       NOT NULL,
    `user_id`     bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_book_user` (`book_id`),
    KEY `fk_idx_user_in_book_user` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `budget`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `date`       date         NOT NULL,
    `money`      double       NOT NULL DEFAULT '0.0',
    `book_id`    bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_budget` (`book_id`)
);

CREATE TABLE IF NOT EXISTS `carry_over`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `date`       date         NOT NULL,
    `money`      double       NOT NULL DEFAULT '0.0',
    `book_id`    bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_carry_over` (`book_id`)
);

CREATE TABLE IF NOT EXISTS `category`
(
    `dtype`      varchar(31)  NOT NULL,
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `name`       varchar(10)  NOT NULL,
    `parent_id`  bigint                DEFAULT NULL,
    `book_id`    bigint                DEFAULT NULL,
    `created_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_category` (`book_id`),
    KEY `fk_idx_category_in_category` (`parent_id`)
);

CREATE TABLE IF NOT EXISTS `settlement`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`        varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `avg_outcome`   double       NOT NULL,
    `end_date`      date         NOT NULL,
    `start_date`    date         NOT NULL,
    `total_outcome` double       NOT NULL,
    `user_count`    int          NOT NULL,
    `book_id`       bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_settlement` (`book_id`)
);

CREATE TABLE IF NOT EXISTS `settlement_user`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`        varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `money`         double       NOT NULL,
    `settlement_id` bigint       NOT NULL,
    `user_id`       bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_settlement_in_settlement_user` (`settlement_id`),
    KEY `fk_idx_user_in_settlement_user` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `signout_other_reason`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `reason`     varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `signout_reason`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`      varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`  datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `count`       bigint       NOT NULL DEFAULT '0',
    `reason_type` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_reason_type_on_signout_reason` (`reason_type`)
);

CREATE TABLE IF NOT EXISTS `user`
(
    `id`                bigint       NOT NULL AUTO_INCREMENT,
    `created_at`        datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`            varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`        datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `email`             varchar(60)  NOT NULL,
    `last_ad_time`      datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `nickname`          varchar(30)  NOT NULL,
    `password`          varchar(100) NOT NULL,
    `profile_img`       varchar(300)          DEFAULT 'user_default',
    `provider_id`       varchar(255)          DEFAULT NULL,
    `provider`          varchar(10)           DEFAULT NULL,
    `recent_book_key`   varchar(45)           DEFAULT NULL,
    `last_login_time`   datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `receive_marketing` tinyint      NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    KEY `idx_email_in_user` (`email`)
);
