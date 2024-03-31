CREATE TABLE IF NOT EXISTS `favorite`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`        varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `user_id`       bigint NOT NULL,
    `book_line_id`  bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_idx_user_in_favorite` (`user_id`),
    KEY `fk_idx_book_line_in_favorite` (`book_line_id`)
);