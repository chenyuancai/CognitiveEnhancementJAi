-- 补齐模型提供商/绑定表 api_key 列（V39 重排后可能未执行 model_provider_api_key）

SET @ddl := (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'qz_ai_model_provider'
              AND COLUMN_NAME = 'api_key'
        ),
        'SELECT 1',
        'ALTER TABLE qz_ai_model_provider ADD COLUMN api_key VARCHAR(512) NULL COMMENT ''API Key（明文，仅服务端使用）'' AFTER default_credential_ref'
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
              AND TABLE_NAME = 'qz_ai_model_provider_binding'
              AND COLUMN_NAME = 'api_key'
        ),
        'SELECT 1',
        'ALTER TABLE qz_ai_model_provider_binding ADD COLUMN api_key VARCHAR(512) NULL COMMENT ''绑定级 API Key 覆盖'' AFTER credential_ref'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
