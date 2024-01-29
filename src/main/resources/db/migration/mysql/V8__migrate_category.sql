-- 기존 데이터를 새 테이블로 마이그레이션
-- 기존 카테고리의 DefaultRoot
INSERT INTO `category`(name)
VALUES ('수입');
INSERT INTO `category`(name)
VALUES ('지출');
INSERT INTO `category`(name)
VALUES ('이체');
INSERT INTO `category`(name)
VALUES ('자산');

-- 기존 카테고리의 Default -> Subcategory에 가계부 마다 추가
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '급여' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '부수입' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '용돈' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '금융소득' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '사업소득' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '상여금' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '기타' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 1 as parent_id, id, '미분류' as name from `book` where status = 'ACTIVE');

INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '식비' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '카페/간식' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '교통' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '주거/통신' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '의료/건강' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '문화' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '여행/숙박' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '생활' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '패션/미용' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '육아' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '교육' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '경조사' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '기타' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 2 as parent_id, id, '미분류' as name from `book` where status = 'ACTIVE');

INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '이체' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '저축' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '현금' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '투자' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '보험' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '카드대금' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '대출' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '기타' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 3 as parent_id, id, '미분류' as name from `book` where status = 'ACTIVE');

INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 4 as parent_id, id, '현금' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 4 as parent_id, id, '체크카드' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 4 as parent_id, id, '신용카드' as name from `book` where status = 'ACTIVE');
INSERT INTO `subcategory`(parent_id, book_id, name)
    (SELECT 4 as parent_id, id, '은행' as name from `book` where status = 'ACTIVE');

-- 기존 카테고리의 BookCategory -> Subcategory
INSERT IGNORE INTO `subcategory`(parent_id, book_id, name) (SELECT parent_id, book_id, name
                                                            FROM `old_category`
                                                            WHERE dtype = 'BookCategory'
                                                              AND status = 'ACTIVE');

-- 기존 BookLineCategory
INSERT INTO `book_line_category`(book_line_id,
                                 line_category_id,
                                 line_subcategory_id,
                                 asset_subcategory_id)
    (SELECT oblc.book_line_id,
            linesub.parent_id,
            linesub.id,
            assetsub.id

     FROM `subcategory` linesub
              INNER JOIN `old_category` old_linesub
                         ON old_linesub.name = linesub.name
                             AND
                            old_linesub.book_id = linesub.book_id
                             AND
                            old_linesub.parent_id = linesub.parent_id
              INNER JOIN `old_book_line_category` oblc
                         ON oblc.category_id = old_linesub.id
              INNER JOIN `old_book_line_category` oblc_asset
                         ON oblc_asset.book_line_id = oblc.book_line_id
              INNER JOIN `old_category` old_assetsub
                         ON old_assetsub.id = oblc_asset.category_id
              INNER JOIN `subcategory` assetsub
                         ON assetsub.name = old_assetsub.name
                             AND
                            assetsub.book_id = old_assetsub.book_id
                             AND assetsub.parent_id =
                                 old_assetsub.parent_id
     WHERE old_linesub.status = 'ACTIVE'
       AND old_linesub.dtype = 'BookCategory'
       AND old_linesub.parent_id < 4
       AND oblc.status = 'ACTIVE'
       AND old_assetsub.status = 'ACTIVE'
       AND old_assetsub.dtype = 'BookCategory'
       AND old_assetsub.parent_id = 4
       AND oblc_asset.status = 'ACTIVE');

-- 기존 테이블 삭제
DROP TABLE `old_category`;
DROP TABLE `old_book_line_category`;



