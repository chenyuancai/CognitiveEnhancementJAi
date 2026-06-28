-- Center 元数据 H2 测试 schema（对齐 Db*Repository + tenant_id）

CREATE TABLE IF NOT EXISTS qz_ai_model_provider (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id               BIGINT       NOT NULL DEFAULT 1,
    provider_code           VARCHAR(64)  NOT NULL,
    provider_name           VARCHAR(128) NOT NULL,
    provider_type           VARCHAR(32)  NOT NULL DEFAULT 'OPENAI_COMPATIBLE',
    default_endpoint        VARCHAR(512),
    default_credential_ref  VARCHAR(128),
    api_key                 VARCHAR(512),
    description             VARCHAR(512),
    status                  VARCHAR(16)  NOT NULL,
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, provider_code)
);

CREATE TABLE IF NOT EXISTS qz_ai_model (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 1,
    model_code          VARCHAR(64)  NOT NULL,
    model_name          VARCHAR(128) NOT NULL,
    model_type          VARCHAR(32)  NOT NULL,
    timeout_ms          INT          NOT NULL,
    retry_times         INT          NOT NULL DEFAULT 0,
    status              VARCHAR(16)  NOT NULL,
    fallback_model_code VARCHAR(64),
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, model_code)
);

CREATE TABLE IF NOT EXISTS qz_ai_model_provider_binding (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    model_code      VARCHAR(64)  NOT NULL,
    provider_code   VARCHAR(64)  NOT NULL,
    endpoint        VARCHAR(512),
    credential_ref  VARCHAR(128),
    api_key         VARCHAR(512),
    route_priority  INT          NOT NULL DEFAULT 0,
    status          VARCHAR(16)  NOT NULL,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, model_code, provider_code)
);

CREATE TABLE IF NOT EXISTS qz_ai_capability_definition (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id              BIGINT       NOT NULL DEFAULT 1,
    capability_code        VARCHAR(64)  NOT NULL,
    capability_name        VARCHAR(128) NOT NULL,
    capability_desc        VARCHAR(512),
    version                VARCHAR(32)  NOT NULL DEFAULT '1.0.0',
    input_schema           VARCHAR(4096),
    output_schema          VARCHAR(4096),
    parameter_constraints  VARCHAR(4096),
    execute_mode           VARCHAR(32)  NOT NULL,
    bound_agent_code       VARCHAR(64)  NOT NULL,
    risk_level             VARCHAR(16)  NOT NULL,
    need_human_confirm     TINYINT      NOT NULL DEFAULT 0,
    status                 VARCHAR(16)  NOT NULL,
    lifecycle_status       VARCHAR(16)  NOT NULL DEFAULT 'PUBLISHED',
    published_at           TIMESTAMP,
    create_time            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, capability_code, version)
);

CREATE TABLE IF NOT EXISTS qz_ai_capability_release_pointer (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL DEFAULT 1,
    capability_code   VARCHAR(64)  NOT NULL,
    baseline_version  VARCHAR(32)  NOT NULL,
    candidate_version VARCHAR(32),
    gray_rule_json    VARCHAR(4096),
    create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, capability_code)
);

CREATE TABLE IF NOT EXISTS qz_ai_capability_tenant_binding (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    capability_code VARCHAR(64)  NOT NULL,
    enabled         TINYINT      NOT NULL DEFAULT 1,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, capability_code)
);

CREATE TABLE IF NOT EXISTS shedlock (
    name       VARCHAR(64)  NOT NULL PRIMARY KEY,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL
);
