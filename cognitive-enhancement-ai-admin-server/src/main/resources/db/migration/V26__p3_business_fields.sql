ALTER TABLE qz_iam_user
    ADD COLUMN user_type VARCHAR(16) NOT NULL DEFAULT 'CUSTOMER' AFTER status;

ALTER TABLE qz_bill_subscription_package
    ADD COLUMN trial_days INT NOT NULL DEFAULT 0 AFTER period_count;

ALTER TABLE qz_bill_subscription
    ADD COLUMN phase VARCHAR(16) NOT NULL DEFAULT 'FORMAL' AFTER status;
