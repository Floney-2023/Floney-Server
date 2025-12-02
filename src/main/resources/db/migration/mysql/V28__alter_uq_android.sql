ALTER TABLE android_subscribe DROP INDEX orderId;

ALTER TABLE android_subscribe
    ADD CONSTRAINT uq_order_status UNIQUE (order_id, status);
