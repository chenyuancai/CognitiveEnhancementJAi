-- 多租户隔离基础字段
-- 默认租户为 default，后续租户管理 API 可在此基础上扩展。

ALTER TABLE sys_user
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE center_model_definition
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE center_prompt_template
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE center_tool_definition
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE center_skill_definition
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE center_agent_definition
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE center_capability_definition
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE runtime_execution_record
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';

ALTER TABLE runtime_usage_record
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码';
