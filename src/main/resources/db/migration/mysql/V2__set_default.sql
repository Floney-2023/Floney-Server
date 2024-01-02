alter table alarm
    modify column status varchar(255) not null default 'ACTIVE';
alter table alarm
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table alarm
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table asset
    modify column status varchar(255) not null default 'ACTIVE';
alter table asset
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table asset
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table book
    modify column status varchar(255) not null default 'ACTIVE';
alter table book
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table book
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table book_line
    modify column status varchar(255) not null default 'ACTIVE';
alter table book_line
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table book_line
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table book_line_category
    modify column status varchar(255) not null default 'ACTIVE';
alter table book_line_category
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table book_line_category
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table book_user
    modify column status varchar(255) not null default 'ACTIVE';
alter table book_user
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table book_user
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table budget
    modify column status varchar(255) not null default 'ACTIVE';
alter table budget
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table budget
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table carry_over
    modify column status varchar(255) not null default 'ACTIVE';
alter table carry_over
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table carry_over
    modify column updated_at datetime(6) not null default current_timestamp(6);

update category
set status = 'ACTIVE'
where dtype in ('Default', 'DefaultRoot');
update category
set created_at = current_timestamp(6),
    updated_at = current_timestamp(6)
where dtype in ('Default', 'DefaultRoot');

alter table category
    modify column status varchar(255) not null default 'ACTIVE';
alter table category
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table category
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table settlement
    modify column status varchar(255) not null default 'ACTIVE';
alter table settlement
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table settlement
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table settlement_user
    modify column status varchar(255) not null default 'ACTIVE';
alter table settlement_user
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table settlement_user
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table signout_other_reason
    modify column status varchar(255) not null default 'ACTIVE';
alter table signout_other_reason
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table signout_other_reason
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table signout_reason
    modify column status varchar(255) not null default 'ACTIVE';
alter table signout_reason
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table signout_reason
    modify column updated_at datetime(6) not null default current_timestamp(6);

alter table user
    modify column status varchar(255) not null default 'ACTIVE';
alter table user
    modify column created_at datetime(6) not null default current_timestamp(6);
alter table user
    modify column updated_at datetime(6) not null default current_timestamp(6);
