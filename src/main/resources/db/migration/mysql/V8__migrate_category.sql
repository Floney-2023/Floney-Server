-- 기존 데이터를 새 테이블로 마이그레이션
-- 기존 카테고리의 DefaultRoot
INSERT INTO `category`(name)
VALUES ('INCOME');
INSERT INTO `category`(name)
VALUES ('OUTCOME');
INSERT INTO `category`(name)
VALUES ('TRANSFER');
INSERT INTO `category`(name)
VALUES ('ASSET');

-- 기존 카테고리의 Default -> Subcategory에 가계부 마다 추가
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '급여' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '부수입' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '용돈' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '금융소득' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '사업소득' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '상여금' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '기타' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 1 as parent_id, id, '미분류' as name, created_at from `book` where status = 'ACTIVE');

INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '식비' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '카페/간식' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '교통' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '주거/통신' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '의료/건강' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '문화' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '여행/숙박' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '생활' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '패션/미용' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '육아' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '교육' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '경조사' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '기타' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 2 as parent_id, id, '미분류' as name, created_at from `book` where status = 'ACTIVE');

INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '이체' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '저축' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '현금' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '투자' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '보험' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '카드대금' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '대출' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '기타' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 3 as parent_id, id, '미분류' as name, created_at from `book` where status = 'ACTIVE');

INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 4 as parent_id, id, '현금' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 4 as parent_id, id, '체크카드' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 4 as parent_id, id, '신용카드' as name, created_at from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name, created_at)
    (SELECT 4 as parent_id, id, '은행' as name, created_at from `book` where status = 'ACTIVE');

-- 기존 카테고리의 BookCategory -> Subcategory
INSERT IGNORE INTO `subcategory`(parent_id, book_id, name, created_at) (SELECT parent_id, book_id, name, created_at
                                                                        FROM `old_category`
                                                                        WHERE dtype = 'BookCategory'
                                                                          AND status = 'ACTIVE');

-- BookLineCategory
insert into `book_line_category` (book_line_id,
                                  line_category_id,
                                  line_subcategory_id,
                                  asset_subcategory_id,
                                  created_at)
    (select bl.id,
            line_sub.parent_id,
            line_sub.id,
            asset_sub.id,
            old_line_sub_blc.created_at

     from old_book_line_category old_line_blc

              -- 3개의 행으로 나뉜 기존 book_line_category를 하나의 행으로 JOIN
              inner join old_book_line_category old_line_sub_blc
                         on old_line_sub_blc.book_line_id = old_line_blc.book_line_id
              inner join old_book_line_category old_asset_sub_blc
                         on old_asset_sub_blc.book_line_id = old_line_sub_blc.book_line_id

         -- book의 id를 찾기 위한 JOIN
              inner join book_line bl
                         on bl.id = old_line_blc.book_line_id

         -- 기존 category의 id를 찾기 위한 JOIN
              inner join old_category old_line_sub
                         on old_line_sub.id = old_line_sub_blc.category_id
              inner join old_category old_asset_sub
                         on old_asset_sub.id = old_asset_sub_blc.category_id

         -- (이름, 속한 가계부, 부모 카테고리)를 바탕으로 새로운 category의 id 를 찾기 위한 JOIN
              inner join subcategory line_sub
                         on line_sub.name = old_line_sub_blc.name
                             and line_sub.parent_id = old_line_blc.category_id
                             and line_sub.book_id = bl.book_id
              inner join subcategory asset_sub
                         on asset_sub.name = old_asset_sub_blc.name
                             and asset_sub.parent_id = 4
                             and asset_sub.book_id = bl.book_id

          -- 3개의 행으로 나뉜 기존 book_line_category를 하나의 행으로 JOIN 하는 조건
     where old_line_blc.book_line_categories_key = 'FLOW'
       and old_line_sub_blc.book_line_categories_key = 'FLOW_LINE'
       and old_asset_sub_blc.book_line_categories_key = 'ASSET'

       and old_line_blc.status = 'ACTIVE'
       and old_line_sub_blc.status = 'ACTIVE'
       and old_asset_sub_blc.status = 'ACTIVE'
       and bl.status = 'ACTIVE'
       and old_line_sub.status = 'ACTIVE'
       and old_asset_sub.status = 'ACTIVE'
       and line_sub.status = 'ACTIVE'
       and asset_sub.status = 'ACTIVE');


-- 기존 테이블 삭제
DROP TABLE `old_category`;
DROP TABLE `old_book_line_category`;



