-- 公告定向：会员等级 / 用户群

ALTER TABLE qz_ops_announcement
    ADD COLUMN target_level_codes VARCHAR(512) NULL COMMENT '定向会员等级编码，逗号分隔；空=不限等级' AFTER publish_at,
    ADD COLUMN target_user_ids VARCHAR(2048) NULL COMMENT '定向用户 ID，逗号分隔；空=不限用户' AFTER target_level_codes;
