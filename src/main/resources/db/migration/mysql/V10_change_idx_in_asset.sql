DROP INDEX `book_and_date_on_asset` ON `asset`;
CREATE UNIQUE INDEX `book_and_date_status_on_asset` ON `asset` (`book_id`, `date`, `status`);
