-- 模型提供商独立表 + 模型主表 + 多对多绑定表；从 qz_ai_model_definition 迁移

CREATE TABLE IF NOT EXISTS qz_ai_model_provider (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_id               BIGINT       NOT NULL DEFAULT 1 COMMENT '租户 ID',
    provider_code           VARCHAR(64)  NOT NULL COMMENT '提供商编码',
    provider_name           VARCHAR(128) NOT NULL COMMENT '提供商名称',
    provider_type           VARCHAR(32)  NOT NULL DEFAULT 'OPENAI_COMPATIBLE' COMMENT '协议类型',
    default_endpoint        VARCHAR(512) COMMENT '默认调用地址',
    default_credential_ref  VARCHAR(128) COMMENT '默认凭证引用',
    description             VARCHAR(512) COMMENT '描述',
    status                  VARCHAR(16)  NOT NULL COMMENT 'ENABLED/DISABLED',
    create_time             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_provider (tenant_id, provider_code),
    INDEX idx_provider_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型提供商';

CREATE TABLE IF NOT EXISTS qz_ai_model (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_id           BIGINT       NOT NULL DEFAULT 1 COMMENT '租户 ID',
    model_code          VARCHAR(64)  NOT NULL COMMENT '模型编码',
    model_name          VARCHAR(128) NOT NULL COMMENT '模型名称',
    model_type          VARCHAR(32)  NOT NULL COMMENT '模型类型',
    timeout_ms          INT          NOT NULL COMMENT '超时(ms)',
    retry_times         INT          NOT NULL DEFAULT 0 COMMENT '重试次数',
    status              VARCHAR(16)  NOT NULL COMMENT 'ENABLED/DISABLED',
    fallback_model_code VARCHAR(64) COMMENT '降级模型编码',
    create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_model (tenant_id, model_code),
    INDEX idx_model_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型主数据';

CREATE TABLE IF NOT EXISTS qz_ai_model_provider_binding (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_id       BIGINT       NOT NULL DEFAULT 1 COMMENT '租户 ID',
    model_code      VARCHAR(64)  NOT NULL COMMENT '模型编码',
    provider_code   VARCHAR(64)  NOT NULL COMMENT '提供商编码',
    endpoint        VARCHAR(512) COMMENT '覆盖 endpoint，空则取提供商默认',
    credential_ref  VARCHAR(128) COMMENT '覆盖凭证引用',
    route_priority  INT          NOT NULL DEFAULT 0 COMMENT '路由优先级',
    status          VARCHAR(16)  NOT NULL COMMENT 'ENABLED/DISABLED',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_model_provider (tenant_id, model_code, provider_code),
    INDEX idx_binding_model (tenant_id, model_code),
    INDEX idx_binding_provider (tenant_id, provider_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型-提供商绑定';

INSERT INTO qz_ai_model_provider (tenant_id, provider_code, provider_name, provider_type, default_endpoint, default_credential_ref, status)
SELECT DISTINCT d.tenant_id,
                d.provider_code,
                d.provider_name,
                CASE
                    WHEN d.provider_code IN ('bailian', 'dashscope') THEN 'DASHSCOPE'
                    WHEN d.provider_code = 'openai' THEN 'OPENAI_COMPATIBLE'
                    ELSE 'OPENAI_COMPATIBLE'
                END,
                d.endpoint,
                d.credential_ref,
                'ENABLED'
FROM qz_ai_model_definition d
WHERE NOT EXISTS (SELECT 1 FROM qz_ai_model_provider LIMIT 1);

INSERT INTO qz_ai_model (tenant_id, model_code, model_name, model_type, timeout_ms, retry_times, status, fallback_model_code)
SELECT d.tenant_id,
       d.model_code,
       d.model_name,
       d.model_type,
       d.timeout_ms,
       d.retry_times,
       d.status,
       d.fallback_model_code
FROM qz_ai_model_definition d
         INNER JOIN (
    SELECT tenant_id, model_code, MIN(id) AS min_id
    FROM qz_ai_model_definition
    GROUP BY tenant_id, model_code
) pick ON d.id = pick.min_id
WHERE NOT EXISTS (SELECT 1 FROM qz_ai_model LIMIT 1);

INSERT INTO qz_ai_model_provider_binding (tenant_id, model_code, provider_code, endpoint, credential_ref, route_priority, status)
SELECT d.tenant_id,
       d.model_code,
       d.provider_code,
       d.endpoint,
       d.credential_ref,
       d.route_priority,
       d.status
FROM qz_ai_model_definition d
WHERE NOT EXISTS (SELECT 1 FROM qz_ai_model_provider_binding LIMIT 1);

DROP TABLE IF EXISTS qz_ai_model_definition;
