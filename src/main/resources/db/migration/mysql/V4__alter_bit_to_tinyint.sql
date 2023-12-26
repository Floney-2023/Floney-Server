ALTER TABLE `alarm`
    CHANGE COLUMN `is_received` `is_received` TINYINT NOT NULL DEFAULT '0';

ALTER TABLE `book`
    CHANGE COLUMN `carry_over_status` `carry_over_status` TINYINT NOT NULL DEFAULT '0';
ALTER TABLE `book`
    CHANGE COLUMN `see_profile` `see_profile` TINYINT NOT NULL DEFAULT '1';

ALTER TABLE `book_line`
    CHANGE COLUMN `except_status` `except_status` TINYINT NOT NULL DEFAULT '0';

ALTER TABLE `user`
    CHANGE COLUMN `receive_marketing` `receive_marketing` TINYINT NOT NULL DEFAULT '0';
