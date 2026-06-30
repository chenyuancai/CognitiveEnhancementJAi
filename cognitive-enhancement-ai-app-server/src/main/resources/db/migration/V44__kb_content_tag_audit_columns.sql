-- qz_kb_content_tag 补齐 BaseEntity 审计字段（V20 建表时遗漏 create_by / update_by）

SET @ddl := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'qz_kb_content_tag'
              AND COLUMN_NAME = 'create_by'
        ),
        'SELECT 1',
        'ALTER TABLE qz_kb_content_tag ADD COLUMN create_by BIGINT NULL COMMENT ''创建人'' AFTER tag_color'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'qz_kb_content_tag'
              AND COLUMN_NAME = 'update_by'
        ),
        'SELECT 1',
        'ALTER TABLE qz_kb_content_tag ADD COLUMN update_by BIGINT NULL COMMENT ''更新人'' AFTER create_by'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
