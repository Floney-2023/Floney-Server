CREATE TABLE asset
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  NOT NULL DEFAULT now(),
    `status`     varchar(255) NOT NULL DEFAULT 'ACTIVE',
    `updated_at` datetime(6)  NOT NULL DEFAULT now(),
    `date`       date         NOT NULL,
    `money`      double       NOT NULL,
    `book_id`    bigint       NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `book_and_date_on_asset` (`book_id`, `date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
