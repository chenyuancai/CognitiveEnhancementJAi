-- 操作记录统一增加中文 message 字段
ALTER TABLE qz_sys_audit_log
    ADD COLUMN message VARCHAR(512) NULL COMMENT '操作说明（中文）' AFTER action;

ALTER TABLE qz_mbr_token_record
    ADD COLUMN message VARCHAR(512) NULL COMMENT '操作说明（中文）' AFTER idempotency_key;

UPDATE qz_mbr_token_record SET message = remark WHERE message IS NULL AND remark IS NOT NULL;

ALTER TABLE qz_mbr_membership_change_log
    ADD COLUMN message VARCHAR(512) NULL COMMENT '操作说明（中文）' AFTER change_type;

UPDATE qz_mbr_membership_change_log SET message = remark WHERE message IS NULL AND remark IS NOT NULL;

ALTER TABLE qz_bill_financial_record
    ADD COLUMN message VARCHAR(512) NULL COMMENT '操作说明（中文）' AFTER record_type;

UPDATE qz_bill_financial_record SET message = remark WHERE message IS NULL AND remark IS NOT NULL;
