truncate table `alarm`;
truncate table `book`;
truncate table `book_line`;
truncate table `book_line_category`;
truncate table `book_user`;
truncate table `budget`;
truncate table `subcategory`;
truncate table `settlement`;
truncate table `settlement_user`;
truncate table `signout_other_reason`;
truncate table `user`;

update `signout_reason`
set count = 0
where status = 'ACTIVE';

