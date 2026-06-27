-- 客服工单域 + 权限种子

CREATE TABLE IF NOT EXISTS qz_ops_support_ticket (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 1,
    ticket_no           VARCHAR(32)  NOT NULL,
    title               VARCHAR(256) NOT NULL,
    body                LONGTEXT,
    category            VARCHAR(64),
    status              VARCHAR(16)  NOT NULL DEFAULT 'OPEN',
    priority            VARCHAR(16)  NOT NULL DEFAULT 'NORMAL',
    submitter_user_id   BIGINT,
    assignee_user_id    BIGINT,
    resolved_at         TIMESTAMP NULL,
    create_by           BIGINT,
    update_by           BIGINT,
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             TINYINT      NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_ticket_no (ticket_no),
    KEY idx_ticket_status (tenant_id, status)
);

INSERT INTO qz_iam_permission (tenant_id, permission_code, alias_code, permission_name, kind, scope, module_key, parent_menu_key, sort_no, builtin, status)
SELECT 1, 'ops:ticket:read', 'admin:ticket:read', '查看客服工单', 'action', 'admin', 'operations', 'support-tickets', 73, 1, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_iam_permission WHERE permission_code = 'ops:ticket:read');

INSERT INTO qz_iam_permission (tenant_id, permission_code, alias_code, permission_name, kind, scope, module_key, parent_menu_key, sort_no, builtin, status)
SELECT 1, 'ops:ticket:update', 'admin:ticket:update', '处理客服工单', 'action', 'admin', 'operations', 'support-tickets', 74, 1, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_iam_permission WHERE permission_code = 'ops:ticket:update');

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code IN ('ops:ticket:read', 'ops:ticket:update')
WHERE r.role_code = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code IN ('ops:ticket:read', 'ops:ticket:update')
WHERE r.role_code = 'SUPPORT'
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);
