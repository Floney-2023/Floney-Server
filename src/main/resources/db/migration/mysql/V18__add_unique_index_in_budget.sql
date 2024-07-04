CREATE UNIQUE INDEX `unique_book_date_status`
    ON `budget` (book_id, date, status);
