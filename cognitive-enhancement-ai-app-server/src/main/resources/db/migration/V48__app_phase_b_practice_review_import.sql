CREATE TABLE IF NOT EXISTS qz_app_practice_session (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL,
    user_id           BIGINT       NOT NULL,
    session_code      VARCHAR(64)  NOT NULL,
    source_content_id BIGINT       NULL,
    title             VARCHAR(256) NOT NULL,
    question_count    INT          NOT NULL DEFAULT 10,
    answered_count    INT          NOT NULL DEFAULT 0,
    status            VARCHAR(16)  NOT NULL DEFAULT 'IN_PROGRESS',
    accuracy          INT          NULL,
    minutes           INT          NULL,
    mode              VARCHAR(32)  NULL,
    debrief_json      TEXT         NULL,
    create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_practice_session_code (tenant_id, session_code),
    KEY idx_practice_session_user (tenant_id, user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_practice_answer (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL,
    session_id          BIGINT       NOT NULL,
    question_id         VARCHAR(64)  NOT NULL,
    question_type       VARCHAR(16)  NOT NULL,
    answer_payload_json TEXT         NOT NULL,
    score               INT          NULL,
    ai_feedback_json    TEXT         NULL,
    status              VARCHAR(16)  NOT NULL DEFAULT 'SUBMITTED',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_practice_answer_session (tenant_id, session_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_review_pending (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL,
    user_id           BIGINT       NOT NULL,
    content_id        BIGINT       NULL,
    title             VARCHAR(256) NOT NULL,
    tag               VARCHAR(64)  NULL,
    accuracy          INT          NULL,
    due_at            TIMESTAMP    NULL,
    urgency           VARCHAR(16)  NOT NULL DEFAULT 'NORMAL',
    status            VARCHAR(16)  NOT NULL DEFAULT 'OPEN',
    source_mistake_id BIGINT       NULL,
    create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_review_pending_user (tenant_id, user_id, status, due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_app_import_task (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    task_code       VARCHAR(64)  NOT NULL,
    channel         VARCHAR(16)  NOT NULL,
    title           VARCHAR(256) NOT NULL,
    file_name       VARCHAR(256) NULL,
    target_type     VARCHAR(64)  NULL,
    tags_json       TEXT         NULL,
    ai_enhanced     TINYINT      NOT NULL DEFAULT 0,
    auto_quiz       TINYINT      NOT NULL DEFAULT 0,
    status          VARCHAR(16)  NOT NULL DEFAULT 'pending',
    stage           VARCHAR(32)  NULL,
    progress        INT          NOT NULL DEFAULT 0,
    error_message   TEXT         NULL,
    library_item_id BIGINT       NULL,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_import_task_code (tenant_id, task_code),
    KEY idx_import_task_user (tenant_id, user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE qz_app_mistake_record
    ADD COLUMN content_id BIGINT NULL AFTER knowledge_point,
    ADD COLUMN score INT NULL AFTER content_id,
    ADD COLUMN tag VARCHAR(64) NULL AFTER score,
    ADD COLUMN source_type VARCHAR(16) NOT NULL DEFAULT 'TUTORING' AFTER tag;
