-- Capability 发布与版本控制：生命周期、多版本唯一约束、发布指针与租户启停

ALTER TABLE center_capability_definition
    ADD COLUMN version VARCHAR(32) NOT NULL DEFAULT '1.0.0' COMMENT '版本号' AFTER capability_desc,
    ADD COLUMN lifecycle_status VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED' COMMENT '生命周期：DRAFT/PUBLISHED/OFFLINE' AFTER status,
    ADD COLUMN published_at DATETIME(3) NULL COMMENT '发布时间' AFTER lifecycle_status;

UPDATE center_capability_definition
SET lifecycle_status = 'PUBLISHED',
    version = '1.0.0',
    published_at = COALESCE(update_time, create_time, CURRENT_TIMESTAMP(3))
WHERE lifecycle_status = 'PUBLISHED';

ALTER TABLE center_capability_definition
    DROP INDEX capability_code;

ALTER TABLE center_capability_definition
    ADD UNIQUE KEY uk_tenant_capability_version (tenant_code, capability_code, version);

CREATE TABLE IF NOT EXISTS center_capability_release_pointer (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_code         VARCHAR(64)  NOT NULL COMMENT '租户编码',
    capability_code     VARCHAR(64)  NOT NULL COMMENT '能力编码',
    baseline_version    VARCHAR(32)  NOT NULL COMMENT '基线版本',
    candidate_version   VARCHAR(32)  NULL COMMENT '灰度候选版本',
    gray_rule_json      JSON         NULL COMMENT '灰度规则 JSON',
    create_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    UNIQUE KEY uk_tenant_capability (tenant_code, capability_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Capability 发布指针';

CREATE TABLE IF NOT EXISTS center_capability_tenant_binding (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_code         VARCHAR(64)  NOT NULL COMMENT '租户编码',
    capability_code     VARCHAR(64)  NOT NULL COMMENT '能力编码',
    enabled             TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用：0-停用 1-启用',
    create_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    UNIQUE KEY uk_tenant_capability (tenant_code, capability_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Capability 租户启停绑定';

-- 观测统计按版本维度扩展
ALTER TABLE runtime_execution_record
    ADD COLUMN capability_version VARCHAR(32) NULL COMMENT '能力版本' AFTER capability_code;

ALTER TABLE runtime_usage_record
    ADD COLUMN capability_version VARCHAR(32) NULL COMMENT '能力版本' AFTER capability_code;
