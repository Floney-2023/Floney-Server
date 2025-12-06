ALTER TABLE android_subscribe
    ADD COLUMN autoResumeTimeMillis VARCHAR(255),
ADD COLUMN auto_renewing TINYINT(1),
ADD COLUMN price_currency_code VARCHAR(255),
ADD COLUMN price_amount_micros VARCHAR(255),
ADD COLUMN auto_resume_time_millis VARCHAR(255),
    ADD COLUMN start_time_millis VARCHAR(255);