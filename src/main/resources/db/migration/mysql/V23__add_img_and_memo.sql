ALTER TABLE `book_line`
    ADD COLUMN memo LONGTEXT;
ALTER TABLE `book_line` ADD COLUMN image_url VARCHAR(255);
ALTER TABLE `repeat_book_line`
    ADD COLUMN memo LONGTEXT;
ALTER TABLE `repeat_book_line` ADD COLUMN image_url VARCHAR(255);