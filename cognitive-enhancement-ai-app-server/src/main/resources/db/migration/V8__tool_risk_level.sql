-- Tool 风险等级字段
-- 已有工具默认 LOW，后续可通过管理接口调整为 MEDIUM/HIGH。

ALTER TABLE center_tool_definition
    ADD COLUMN risk_level VARCHAR(16) NOT NULL DEFAULT 'LOW' COMMENT '风险等级：LOW/MEDIUM/HIGH';
