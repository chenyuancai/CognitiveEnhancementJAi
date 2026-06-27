ALTER TABLE qz_bill_subscription_package
    ADD COLUMN daily_limit BIGINT NOT NULL DEFAULT 0 AFTER cycle_token_quota;

ALTER TABLE qz_bill_subscription_package
    ADD COLUMN concurrent_limit INT NOT NULL DEFAULT 0 AFTER daily_limit;

ALTER TABLE qz_bill_subscription_package
    ADD COLUMN model_scope VARCHAR(256) AFTER concurrent_limit;
