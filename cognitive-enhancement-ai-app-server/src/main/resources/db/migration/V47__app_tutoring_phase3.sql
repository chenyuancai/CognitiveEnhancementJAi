CREATE TABLE IF NOT EXISTS qz_app_learning_profile (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL,
    user_id      BIGINT       NOT NULL,
    profile_json TEXT         NOT NULL,
    version_no   INT          NOT NULL DEFAULT 1,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_app_learning_profile (tenant_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_mistake_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    session_id      VARCHAR(64)  NOT NULL,
    trace_id        VARCHAR(64)  NOT NULL,
    knowledge_point VARCHAR(128) NULL,
    mistake_summary TEXT         NOT NULL,
    user_approach   TEXT         NULL,
    correction_hint TEXT         NULL,
    status          VARCHAR(16)  NOT NULL DEFAULT 'OPEN',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_mistake_user (tenant_id, user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_learning_plan (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    session_id  VARCHAR(64)  NULL,
    trace_id    VARCHAR(64)  NOT NULL,
    plan_title  VARCHAR(128) NOT NULL,
    plan_json   TEXT         NOT NULL,
    status      VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_learning_plan_user (tenant_id, user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_practice_recommendation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    session_id      VARCHAR(64)  NOT NULL,
    trace_id        VARCHAR(64)  NOT NULL,
    knowledge_point VARCHAR(128) NULL,
    prompt_text     TEXT         NOT NULL,
    difficulty      VARCHAR(16)  NOT NULL DEFAULT 'EASY',
    status          VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_practice_user (tenant_id, user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_message_reference (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    session_id  VARCHAR(64)  NOT NULL,
    trace_id    VARCHAR(64)  NOT NULL,
    message_id  VARCHAR(64)  NULL,
    ref_type    VARCHAR(16)  NOT NULL,
    ref_id      VARCHAR(64)  NOT NULL,
    excerpt     VARCHAR(512) NULL,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_msg_ref_session (tenant_id, session_id, trace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
