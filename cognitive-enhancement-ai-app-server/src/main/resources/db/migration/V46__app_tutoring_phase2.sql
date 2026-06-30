CREATE TABLE IF NOT EXISTS qz_app_conversation_summary (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL,
    session_id   VARCHAR(64)  NOT NULL,
    summary_text TEXT         NOT NULL,
    version_no   INT          NOT NULL DEFAULT 1,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_app_conversation_summary (tenant_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_tutoring_blueprint (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id      BIGINT       NOT NULL,
    session_id     VARCHAR(64)  NOT NULL,
    trace_id       VARCHAR(64)  NOT NULL,
    message_id     VARCHAR(64)  NULL,
    intent         VARCHAR(64)  NULL,
    strategy       VARCHAR(64)  NULL,
    blueprint_json TEXT         NOT NULL,
    create_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_app_tutoring_blueprint_session (tenant_id, session_id, create_time),
    KEY idx_app_tutoring_blueprint_trace (trace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_learning_state_snapshot (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    session_id  VARCHAR(64)  NOT NULL,
    trace_id    VARCHAR(64)  NOT NULL,
    state_json  TEXT         NOT NULL,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_app_learning_state_session (tenant_id, session_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
