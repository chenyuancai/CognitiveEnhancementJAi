-- Center 元数据：tenant_code → tenant_id（对齐 sys_tenant.id，平台租户 id=1）

-- ==================== center_model_definition ====================
ALTER TABLE center_model_definition
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_model_definition
SET tenant_id = 1
WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;

UPDATE center_model_definition c
    INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code
SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_model_definition DROP COLUMN tenant_code;
ALTER TABLE center_model_definition ADD KEY idx_tenant_id (tenant_id);

-- ==================== center_tool_definition ====================
ALTER TABLE center_tool_definition
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_tool_definition SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_tool_definition c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_tool_definition DROP COLUMN tenant_code;
ALTER TABLE center_tool_definition ADD KEY idx_tenant_id (tenant_id);

-- ==================== center_skill_definition ====================
ALTER TABLE center_skill_definition
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_skill_definition SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_skill_definition c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_skill_definition DROP COLUMN tenant_code;
ALTER TABLE center_skill_definition ADD KEY idx_tenant_id (tenant_id);

-- ==================== center_agent_definition ====================
ALTER TABLE center_agent_definition
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_agent_definition SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_agent_definition c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_agent_definition DROP COLUMN tenant_code;
ALTER TABLE center_agent_definition ADD KEY idx_tenant_id (tenant_id);

-- ==================== center_prompt_template ====================
ALTER TABLE center_prompt_template DROP INDEX uk_tenant_prompt_version;

ALTER TABLE center_prompt_template
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_prompt_template SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_prompt_template c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_prompt_template DROP COLUMN tenant_code;
ALTER TABLE center_prompt_template ADD UNIQUE KEY uk_tenant_prompt_version (tenant_id, prompt_code, version);
ALTER TABLE center_prompt_template ADD KEY idx_tenant_id (tenant_id);

-- ==================== center_capability_definition ====================
ALTER TABLE center_capability_definition DROP INDEX uk_tenant_capability_version;

ALTER TABLE center_capability_definition
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_capability_definition SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_capability_definition c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_capability_definition DROP COLUMN tenant_code;
ALTER TABLE center_capability_definition ADD UNIQUE KEY uk_tenant_capability_version (tenant_id, capability_code, version);
ALTER TABLE center_capability_definition ADD KEY idx_tenant_id (tenant_id);

-- ==================== center_prompt_release_pointer ====================
ALTER TABLE center_prompt_release_pointer DROP INDEX uk_tenant_prompt;

ALTER TABLE center_prompt_release_pointer
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_prompt_release_pointer SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_prompt_release_pointer c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_prompt_release_pointer DROP COLUMN tenant_code;
ALTER TABLE center_prompt_release_pointer ADD UNIQUE KEY uk_tenant_prompt (tenant_id, prompt_code);

-- ==================== center_capability_release_pointer ====================
ALTER TABLE center_capability_release_pointer DROP INDEX uk_tenant_capability;

ALTER TABLE center_capability_release_pointer
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_capability_release_pointer SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_capability_release_pointer c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_capability_release_pointer DROP COLUMN tenant_code;
ALTER TABLE center_capability_release_pointer ADD UNIQUE KEY uk_tenant_capability (tenant_id, capability_code);

-- ==================== center_capability_tenant_binding ====================
ALTER TABLE center_capability_tenant_binding DROP INDEX uk_tenant_capability;

ALTER TABLE center_capability_tenant_binding
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE center_capability_tenant_binding SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;
UPDATE center_capability_tenant_binding c INNER JOIN sys_tenant t ON c.tenant_code = t.tenant_code SET c.tenant_id = t.id
WHERE c.tenant_code NOT IN ('default', 'platform');

ALTER TABLE center_capability_tenant_binding DROP COLUMN tenant_code;
ALTER TABLE center_capability_tenant_binding ADD UNIQUE KEY uk_tenant_capability (tenant_id, capability_code);

-- ==================== runtime 执行/用量（V9 租户字段） ====================
ALTER TABLE runtime_execution_record
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_execution_record SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;

ALTER TABLE runtime_execution_record DROP COLUMN tenant_code;
ALTER TABLE runtime_execution_record ADD KEY idx_tenant_id (tenant_id);

ALTER TABLE runtime_usage_record
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT '租户ID' AFTER id;

UPDATE runtime_usage_record SET tenant_id = 1 WHERE tenant_code IN ('default', 'platform') OR tenant_code IS NULL;

ALTER TABLE runtime_usage_record DROP COLUMN tenant_code;
ALTER TABLE runtime_usage_record ADD KEY idx_tenant_id (tenant_id);
