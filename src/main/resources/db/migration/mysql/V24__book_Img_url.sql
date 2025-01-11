ALTER TABLE `book_line` DROP COLUMN `image_url`;
ALTER TABLE `repeat_book_line` DROP COLUMN `image_url`;

CREATE TABLE  IF NOT EXISTS `book_line_img` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `book_line_id` BIGINT,
    `img_url` VARCHAR(255) NOT NULL,
    `created_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`        varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`    datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `repeat_book_line_id`  BIGINT,
    KEY `fk_idx_book_line_id` (`book_line_id`),
    KEY `fk_idx_repeat_book_line_id` (`repeat_book_line_id`)
);