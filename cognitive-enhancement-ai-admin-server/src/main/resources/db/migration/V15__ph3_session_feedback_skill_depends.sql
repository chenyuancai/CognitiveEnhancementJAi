-- PH3: Skill 依赖、会话与反馈
ALTER TABLE center_skill_definition
    ADD COLUMN depends_on_skill_codes JSON NULL COMMENT '依赖 Skill 编码列表' AFTER examples;

CREATE TABLE IF NOT EXISTS runtime_conversation_session (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code     VARCHAR(64)    NOT NULL COMMENT '租户编码',
    session_id      VARCHAR(64)    NOT NULL COMMENT '会话ID',
    user_id         VARCHAR(128)   NOT NULL COMMENT '用户ID',
    capability_code VARCHAR(128)   NOT NULL COMMENT '能力编码',
    title           VARCHAR(256)   NULL COMMENT '会话标题',
    status          VARCHAR(32)    NOT NULL COMMENT '会话状态',
    created_at      DATETIME(3)    NOT NULL COMMENT '创建时间',
    updated_at      DATETIME(3)    NOT NULL COMMENT '更新时间',
    create_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_conversation_session (tenant_code, session_id),
    KEY idx_conversation_session_user (tenant_code, user_id),
    KEY idx_conversation_session_capability (tenant_code, capability_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 会话';

CREATE TABLE IF NOT EXISTS runtime_conversation_message (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code     VARCHAR(64)    NOT NULL COMMENT '租户编码',
    message_id      VARCHAR(64)    NOT NULL COMMENT '消息ID',
    session_id      VARCHAR(64)    NOT NULL COMMENT '会话ID',
    role            VARCHAR(32)    NOT NULL COMMENT '消息角色',
    content         TEXT           NOT NULL COMMENT '消息内容',
    trace_id        VARCHAR(128)   NULL COMMENT '关联 TraceId',
    recorded_at     DATETIME(3)    NOT NULL COMMENT '记录时间',
    create_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_conversation_message (tenant_code, message_id),
    KEY idx_conversation_message_session (tenant_code, session_id, recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 会话消息';

CREATE TABLE IF NOT EXISTS runtime_execution_feedback (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code      VARCHAR(64)    NOT NULL COMMENT '租户编码',
    feedback_id      VARCHAR(64)    NOT NULL COMMENT '反馈ID',
    trace_id         VARCHAR(128)   NOT NULL COMMENT 'TraceId',
    session_id       VARCHAR(64)    NULL COMMENT '会话ID',
    rating           INT            NULL COMMENT '评分 1-5',
    original_answer  TEXT           NULL COMMENT 'AI 原始回答',
    corrected_answer TEXT           NULL COMMENT '用户修正回答',
    comment_text     TEXT           NULL COMMENT '反馈备注',
    recorded_at      DATETIME(3)    NOT NULL COMMENT '记录时间',
    create_time      DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time      DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_execution_feedback (tenant_code, feedback_id),
    KEY idx_execution_feedback_trace (tenant_code, trace_id),
    KEY idx_execution_feedback_session (tenant_code, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 执行反馈';
