-- 内置角色 OPERATOR / CONTENT / SUPPORT 权限绑定（ADMIN 已在 V20 绑全量）

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code IN (
    'workbench:view', 'membership:level:update', 'membership:level:grant',
    'billing:order:update', 'billing:order:export',
    'ops:banner:create', 'ops:banner:update', 'system:dict:read',
    'menu:workbench', 'menu:orders'
    )
WHERE r.role_code = 'OPERATOR'
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code IN (
    'workbench:view', 'content:item:update', 'content:item:audit', 'menu:workbench'
    )
WHERE r.role_code = 'CONTENT'
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code IN (
    'workbench:view', 'iam:user:read', 'billing:order:export',
    'menu:workbench', 'menu:orders'
    )
WHERE r.role_code = 'SUPPORT'
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code = 'workbench:view'
WHERE r.role_code IN ('OPERATOR', 'CONTENT', 'SUPPORT')
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);
