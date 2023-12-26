ALTER TABLE `book`
    CHANGE COLUMN `carry_over_money` `carry_over_money` DOUBLE NOT NULL DEFAULT '0.0';

ALTER TABLE `book`
    CHANGE COLUMN `asset` `asset` DOUBLE NOT NULL DEFAULT '0.0';

ALTER TABLE `settlement_user`
    CHANGE COLUMN `money` `money` DOUBLE NOT NULL;
