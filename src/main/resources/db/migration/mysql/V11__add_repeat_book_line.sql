CREATE TABLE IF NOT EXISTS `repeat_book_line`
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
    `repeat_duration` varchar(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_book_in_repeat_book_line` (`book_id`),
    KEY `fk_idx_user_in_repeat_book_line` (`writer_id`)
);
