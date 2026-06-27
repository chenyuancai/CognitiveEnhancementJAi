-- Prompt 发布与灰度：生命周期、多版本唯一约束、发布指针

ALTER TABLE center_prompt_template
    ADD COLUMN lifecycle_status VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED' COMMENT '生命周期：DRAFT/PUBLISHED/OFFLINE' AFTER status,
    ADD COLUMN gray_rule_json JSON NULL COMMENT '版本级灰度规则快照（可选）' AFTER lifecycle_status;

UPDATE center_prompt_template
SET lifecycle_status = CASE
    WHEN published_at IS NOT NULL THEN 'PUBLISHED'
    ELSE 'DRAFT'
END
WHERE lifecycle_status = 'PUBLISHED';

ALTER TABLE center_prompt_template
    DROP INDEX prompt_code;

ALTER TABLE center_prompt_template
    ADD UNIQUE KEY uk_tenant_prompt_version (tenant_code, prompt_code, version);

CREATE TABLE IF NOT EXISTS center_prompt_release_pointer (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_code         VARCHAR(64)  NOT NULL COMMENT '租户编码',
    prompt_code         VARCHAR(64)  NOT NULL COMMENT 'Prompt 编码',
    baseline_version    VARCHAR(32)  NOT NULL COMMENT '基线版本',
    candidate_version   VARCHAR(32)  NULL COMMENT '灰度候选版本',
    gray_rule_json      JSON         NULL COMMENT '灰度规则 JSON',
    create_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    UNIQUE KEY uk_tenant_prompt (tenant_code, prompt_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Prompt 发布指针';
