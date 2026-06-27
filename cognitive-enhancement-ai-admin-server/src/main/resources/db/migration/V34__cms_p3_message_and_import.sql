-- CMS P3：站内信表 + 导入任务 CSV 原文

CREATE TABLE IF NOT EXISTS qz_ops_in_app_message (
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    user_id       BIGINT       NOT NULL COMMENT '收件用户 ID',
    template_code VARCHAR(64)  DEFAULT NULL,
    title         VARCHAR(128) DEFAULT NULL,
    content       TEXT         NOT NULL,
    read_flag     TINYINT      NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted       TINYINT      NOT NULL DEFAULT 0,
    KEY idx_in_app_user (tenant_id, user_id, read_flag, id)
) COMMENT='站内信';

ALTER TABLE qz_kb_content_import_job
    ADD COLUMN source_content MEDIUMTEXT NULL COMMENT 'CSV 原文（异步解析）';
