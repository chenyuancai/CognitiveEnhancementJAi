-- 会员权益规范化目录 + 等级权益值

CREATE TABLE IF NOT EXISTS qz_mbr_benefit_def (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    benefit_code  VARCHAR(64)  NOT NULL,
    benefit_name  VARCHAR(128) NOT NULL,
    category      VARCHAR(16)  NOT NULL,
    value_type    VARCHAR(16)  NOT NULL,
    unit          VARCHAR(16),
    description   VARCHAR(512),
    status        VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_benefit_code (tenant_id, benefit_code)
);

CREATE TABLE IF NOT EXISTS qz_mbr_level_benefit (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    level_id      BIGINT       NOT NULL,
    benefit_code  VARCHAR(64)  NOT NULL,
    benefit_value VARCHAR(128) NOT NULL,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_level_benefit (level_id, benefit_code)
);

INSERT INTO qz_mbr_benefit_def (tenant_id, benefit_code, benefit_name, category, value_type, unit) VALUES
    (1, 'ai.scoring', 'AI 评分', 'FUNCTION', 'BOOL', NULL),
    (1, 'ai.tutoring', 'AI 带学', 'FUNCTION', 'BOOL', NULL),
    (1, 'ai.qa_global', '全局 AI 问答', 'FUNCTION', 'BOOL', NULL),
    (1, 'kb.capacity', '知识库容量', 'CONTENT', 'NUMBER', 'MB'),
    (1, 'kb.import_channels', '可用导入渠道数', 'CONTENT', 'NUMBER', '个'),
    (1, 'kb.file_max_size', '单文件大小上限', 'CONTENT', 'NUMBER', 'MB'),
    (1, 'usage.monthly_token', '每月基础 Token', 'USAGE', 'NUMBER', 'token'),
    (1, 'usage.daily_limit', '每日 Token 上限', 'USAGE', 'NUMBER', 'token'),
    (1, 'service.scoring_priority', '评分队列优先级', 'SERVICE', 'ENUM', NULL),
    (1, 'service.support_priority', '客服优先级', 'SERVICE', 'ENUM', NULL);

INSERT INTO qz_mbr_level_benefit (tenant_id, level_id, benefit_code, benefit_value)
SELECT 1, l.id, b.benefit_code, b.benefit_value
FROM qz_mbr_level l
         JOIN (
    SELECT 'FREE' AS level_code, 'ai.scoring' AS benefit_code, 'false' AS benefit_value UNION ALL
    SELECT 'FREE', 'ai.tutoring', 'false' UNION ALL
    SELECT 'FREE', 'ai.qa_global', 'true' UNION ALL
    SELECT 'FREE', 'kb.capacity', '512' UNION ALL
    SELECT 'FREE', 'kb.import_channels', '1' UNION ALL
    SELECT 'FREE', 'kb.file_max_size', '10' UNION ALL
    SELECT 'FREE', 'usage.monthly_token', '100000' UNION ALL
    SELECT 'FREE', 'usage.daily_limit', '20000' UNION ALL
    SELECT 'FREE', 'service.scoring_priority', 'LOW' UNION ALL
    SELECT 'FREE', 'service.support_priority', 'NORMAL' UNION ALL
    SELECT 'PRO', 'ai.scoring', 'true' UNION ALL
    SELECT 'PRO', 'ai.tutoring', 'true' UNION ALL
    SELECT 'PRO', 'ai.qa_global', 'true' UNION ALL
    SELECT 'PRO', 'kb.capacity', '5120' UNION ALL
    SELECT 'PRO', 'kb.import_channels', '4' UNION ALL
    SELECT 'PRO', 'kb.file_max_size', '50' UNION ALL
    SELECT 'PRO', 'usage.monthly_token', '1000000' UNION ALL
    SELECT 'PRO', 'usage.daily_limit', '200000' UNION ALL
    SELECT 'PRO', 'service.scoring_priority', 'HIGH' UNION ALL
    SELECT 'PRO', 'service.support_priority', 'HIGH'
) b ON b.level_code = l.level_code
WHERE NOT EXISTS (
    SELECT 1 FROM qz_mbr_level_benefit lb WHERE lb.level_id = l.id AND lb.benefit_code = b.benefit_code
);

UPDATE qz_mbr_level SET benefits_json = '{"ai.scoring":false,"ai.tutoring":false,"ai.qa_global":true}' WHERE level_code = 'FREE';
UPDATE qz_mbr_level SET benefits_json = '{"ai.scoring":true,"ai.tutoring":true,"ai.qa_global":true}' WHERE level_code = 'PRO';
