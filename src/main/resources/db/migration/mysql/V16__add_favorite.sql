CREATE TABLE IF NOT EXISTS `favorite`
(
    `id`                   bigint       NOT NULL AUTO_INCREMENT,
    `created_at`           datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `status`               varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at`           datetime(6)  NOT NULL DEFAULT current_timestamp(6),
    `book_id`              bigint       NOT NULL,
    `money`                double       NOT NULL,
    `description`          varchar(255),
    `line_category_id`     bigint       NOT NULL,
    `line_subcategory_id`  bigint       NOT NULL,
    `asset_subcategory_id` bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_book_in_favorite` (`book_id`),
    KEY `fk_line_category_in_favorite` (`line_category_id`),
    KEY `fk_line_subcategory_in_favorite` (`line_subcategory_id`),
    KEY `fk_asset_subcategory_in_favorite` (`asset_subcategory_id`)
);
