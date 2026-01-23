-- Add category_key column for i18n support
-- This migration adds English key fields to category tables for internationalization

-- Step 1: Add category_key column to default_subcategory table
ALTER TABLE `default_subcategory`
ADD COLUMN `category_key` varchar(100) NULL COMMENT 'i18n key for category name'
AFTER `name`;

-- Step 2: Update existing records with English keys (from Figma)
-- 수입 (INCOME, parent_id=1) - 8 categories
UPDATE `default_subcategory` SET `category_key` = 'Salary' WHERE `parent_id` = 1 AND `name` = '급여' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Extra Income' WHERE `parent_id` = 1 AND `name` = '부수입' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Allowance' WHERE `parent_id` = 1 AND `name` = '용돈' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Financial Income' WHERE `parent_id` = 1 AND `name` = '금융소득' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Business Income' WHERE `parent_id` = 1 AND `name` = '사업소득' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Bonus' WHERE `parent_id` = 1 AND `name` = '상여금' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Other' WHERE `parent_id` = 1 AND `name` = '기타' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Uncategorized' WHERE `parent_id` = 1 AND `name` = '미분류' AND `status` = 'ACTIVE';

-- 지출 (OUTCOME, parent_id=2) - 14 categories
UPDATE `default_subcategory` SET `category_key` = 'Food' WHERE `parent_id` = 2 AND `name` = '식비' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Cafe/Snacks' WHERE `parent_id` = 2 AND `name` = '카페/간식' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Transport' WHERE `parent_id` = 2 AND `name` = '교통' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Housing/Phone' WHERE `parent_id` = 2 AND `name` = '주거/통신' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Health' WHERE `parent_id` = 2 AND `name` = '의료/건강' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Culture' WHERE `parent_id` = 2 AND `name` = '문화' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Travel/Stay' WHERE `parent_id` = 2 AND `name` = '여행/숙박' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Living' WHERE `parent_id` = 2 AND `name` = '생활' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Style/Beauty' WHERE `parent_id` = 2 AND `name` = '패션/미용' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Family' WHERE `parent_id` = 2 AND `name` = '육아' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Education' WHERE `parent_id` = 2 AND `name` = '교육' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Events' WHERE `parent_id` = 2 AND `name` = '경조사' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Other' WHERE `parent_id` = 2 AND `name` = '기타' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Uncategorized' WHERE `parent_id` = 2 AND `name` = '미분류' AND `status` = 'ACTIVE';

-- 이체 (TRANSFER, parent_id=3) - 9 categories
UPDATE `default_subcategory` SET `category_key` = 'Transfer' WHERE `parent_id` = 3 AND `name` = '이체' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Savings' WHERE `parent_id` = 3 AND `name` = '저축' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Cash' WHERE `parent_id` = 3 AND `name` = '현금' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Investment' WHERE `parent_id` = 3 AND `name` = '투자' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Insurance' WHERE `parent_id` = 3 AND `name` = '보험' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Card Payment' WHERE `parent_id` = 3 AND `name` = '카드대금' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Loan' WHERE `parent_id` = 3 AND `name` = '대출' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Other' WHERE `parent_id` = 3 AND `name` = '기타' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Uncategorized' WHERE `parent_id` = 3 AND `name` = '미분류' AND `status` = 'ACTIVE';

-- 자산 (ASSET, parent_id=4) - 4 categories
UPDATE `default_subcategory` SET `category_key` = 'Cash' WHERE `parent_id` = 4 AND `name` = '현금' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Debit Card' WHERE `parent_id` = 4 AND `name` = '체크카드' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Credit Card' WHERE `parent_id` = 4 AND `name` = '신용카드' AND `status` = 'ACTIVE';
UPDATE `default_subcategory` SET `category_key` = 'Bank' WHERE `parent_id` = 4 AND `name` = '은행' AND `status` = 'ACTIVE';

-- Step 3: Add category_key column to subcategory table
ALTER TABLE `subcategory`
ADD COLUMN `category_key` varchar(100) NULL COMMENT 'i18n key for category name (null for user-defined categories)'
AFTER `name`;

-- Step 4: Copy category_key from default_subcategory to existing subcategories
-- This will match by name and parent_id, but only for records that came from default_subcategory
UPDATE `subcategory` s
INNER JOIN `default_subcategory` ds ON s.name = ds.name AND s.parent_id = ds.parent_id AND ds.status = 'ACTIVE'
SET s.category_key = ds.category_key
WHERE s.status = 'ACTIVE' AND ds.category_key IS NOT NULL;

-- Step 5: Add unique constraint for category_key in default_subcategory
-- Note: category_key can be NULL for future custom categories, but must be unique per parent when not NULL
ALTER TABLE `default_subcategory`
ADD UNIQUE KEY `unique_category_key_status_in_default_subcategory` (`parent_id`, `category_key`, `status`);

-- Step 6: Add index on category_key for better query performance
ALTER TABLE `subcategory`
ADD KEY `idx_category_key` (`category_key`);
