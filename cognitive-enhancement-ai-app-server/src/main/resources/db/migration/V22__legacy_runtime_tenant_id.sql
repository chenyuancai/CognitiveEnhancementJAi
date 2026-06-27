-- Legacy Runtime 表：tenant_code → tenant_id（对齐 sys_tenant.id，平台租户 id=1）

-- ==================== runtime_conversation_session ====================
ALTER TABLE runtime_conversation_session DROP INDEX uk_conversation_session;
ALTER TABLE runtime_conversation_session DROP INDEX idx_conversation_session_user;
ALTER TABLE runtime_conversation_session DROP INDEX idx_conversation_session_capability;

ALTER TABLE runtime_conversation_session
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_conversation_session SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_conversation_session s INNER JOIN sys_tenant t ON s.tenant_code = t.tenant_code SET s.tenant_id = t.id
WHERE s.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_conversation_session DROP COLUMN tenant_code;
ALTER TABLE runtime_conversation_session ADD UNIQUE KEY uk_conversation_session (tenant_id, session_id);
ALTER TABLE runtime_conversation_session ADD KEY idx_conversation_session_user (tenant_id, user_id);
ALTER TABLE runtime_conversation_session ADD KEY idx_conversation_session_capability (tenant_id, capability_code);

-- ==================== runtime_conversation_message ====================
ALTER TABLE runtime_conversation_message DROP INDEX uk_conversation_message;
ALTER TABLE runtime_conversation_message DROP INDEX idx_conversation_message_session;

ALTER TABLE runtime_conversation_message
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_conversation_message SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_conversation_message m INNER JOIN sys_tenant t ON m.tenant_code = t.tenant_code SET m.tenant_id = t.id
WHERE m.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_conversation_message DROP COLUMN tenant_code;
ALTER TABLE runtime_conversation_message ADD UNIQUE KEY uk_conversation_message (tenant_id, message_id);
ALTER TABLE runtime_conversation_message ADD KEY idx_conversation_message_session (tenant_id, session_id, recorded_at);

-- ==================== runtime_execution_feedback ====================
ALTER TABLE runtime_execution_feedback DROP INDEX uk_execution_feedback;
ALTER TABLE runtime_execution_feedback DROP INDEX idx_execution_feedback_trace;
ALTER TABLE runtime_execution_feedback DROP INDEX idx_execution_feedback_session;

ALTER TABLE runtime_execution_feedback
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_execution_feedback SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_execution_feedback f INNER JOIN sys_tenant t ON f.tenant_code = t.tenant_code SET f.tenant_id = t.id
WHERE f.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_execution_feedback DROP COLUMN tenant_code;
ALTER TABLE runtime_execution_feedback ADD UNIQUE KEY uk_execution_feedback (tenant_id, feedback_id);
ALTER TABLE runtime_execution_feedback ADD KEY idx_execution_feedback_trace (tenant_id, trace_id);
ALTER TABLE runtime_execution_feedback ADD KEY idx_execution_feedback_session (tenant_id, session_id);

-- ==================== runtime_knowledge_fragment ====================
ALTER TABLE runtime_knowledge_fragment DROP INDEX uk_knowledge_fragment;
ALTER TABLE runtime_knowledge_fragment DROP INDEX idx_knowledge_fragment_code;

ALTER TABLE runtime_knowledge_fragment
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_knowledge_fragment SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_knowledge_fragment k INNER JOIN sys_tenant t ON k.tenant_code = t.tenant_code SET k.tenant_id = t.id
WHERE k.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_knowledge_fragment DROP COLUMN tenant_code;
ALTER TABLE runtime_knowledge_fragment ADD UNIQUE KEY uk_knowledge_fragment (tenant_id, fragment_id);
ALTER TABLE runtime_knowledge_fragment ADD KEY idx_knowledge_fragment_code (tenant_id, knowledge_code);

-- ==================== runtime_scenario_knowledge_binding ====================
ALTER TABLE runtime_scenario_knowledge_binding DROP INDEX uk_scenario_knowledge_binding;
ALTER TABLE runtime_scenario_knowledge_binding DROP INDEX idx_scenario_knowledge_binding_scenario;

ALTER TABLE runtime_scenario_knowledge_binding
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_scenario_knowledge_binding SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_scenario_knowledge_binding b INNER JOIN sys_tenant t ON b.tenant_code = t.tenant_code SET b.tenant_id = t.id
WHERE b.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_scenario_knowledge_binding DROP COLUMN tenant_code;
ALTER TABLE runtime_scenario_knowledge_binding ADD UNIQUE KEY uk_scenario_knowledge_binding (tenant_id, binding_id);
ALTER TABLE runtime_scenario_knowledge_binding ADD KEY idx_scenario_knowledge_binding_scenario (tenant_id, scenario_code);

-- ==================== runtime_file_upload_record ====================
ALTER TABLE runtime_file_upload_record DROP INDEX uk_file_upload_record;

ALTER TABLE runtime_file_upload_record
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_file_upload_record SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_file_upload_record f INNER JOIN sys_tenant t ON f.tenant_code = t.tenant_code SET f.tenant_id = t.id
WHERE f.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_file_upload_record DROP COLUMN tenant_code;
ALTER TABLE runtime_file_upload_record ADD UNIQUE KEY uk_file_upload_record (tenant_id, file_id);

-- ==================== runtime_file_parse_task ====================
ALTER TABLE runtime_file_parse_task DROP INDEX uk_file_parse_task;
ALTER TABLE runtime_file_parse_task DROP INDEX idx_file_parse_task_file;

ALTER TABLE runtime_file_parse_task
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_file_parse_task SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_file_parse_task p INNER JOIN sys_tenant t ON p.tenant_code = t.tenant_code SET p.tenant_id = t.id
WHERE p.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_file_parse_task DROP COLUMN tenant_code;
ALTER TABLE runtime_file_parse_task ADD UNIQUE KEY uk_file_parse_task (tenant_id, task_id);
ALTER TABLE runtime_file_parse_task ADD KEY idx_file_parse_task_file (tenant_id, file_id, finished_at);

-- ==================== runtime_audit_log ====================
ALTER TABLE runtime_audit_log DROP INDEX idx_audit_tenant_code;

ALTER TABLE runtime_audit_log
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_audit_log SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_audit_log a INNER JOIN sys_tenant t ON a.tenant_code = t.tenant_code SET a.tenant_id = t.id
WHERE a.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_audit_log DROP COLUMN tenant_code;
ALTER TABLE runtime_audit_log ADD KEY idx_audit_tenant_id (tenant_id);

-- ==================== runtime_usage_account ====================
ALTER TABLE runtime_usage_account DROP INDEX uk_usage_account_tenant_code;

ALTER TABLE runtime_usage_account
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_usage_account SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_usage_account u INNER JOIN sys_tenant t ON u.tenant_code = t.tenant_code SET u.tenant_id = t.id
WHERE u.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_usage_account DROP COLUMN tenant_code;
ALTER TABLE runtime_usage_account ADD UNIQUE KEY uk_usage_account_tenant_id (tenant_id);

-- ==================== runtime_trace_span ====================
ALTER TABLE runtime_trace_span DROP INDEX idx_trace_span_tenant_trace;

ALTER TABLE runtime_trace_span
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_trace_span SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE runtime_trace_span s INNER JOIN sys_tenant t ON s.tenant_code = t.tenant_code SET s.tenant_id = t.id
WHERE s.tenant_code NOT IN ('default', 'platform');

ALTER TABLE runtime_trace_span DROP COLUMN tenant_code;
ALTER TABLE runtime_trace_span ADD KEY idx_trace_span_tenant_trace (tenant_id, trace_id);
