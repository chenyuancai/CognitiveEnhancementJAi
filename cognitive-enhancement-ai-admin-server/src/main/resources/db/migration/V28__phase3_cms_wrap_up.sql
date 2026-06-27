-- Phase 3 收尾：工作台权限、Banner 位字典、内容版本历史

INSERT INTO qz_iam_permission (tenant_id, permission_code, alias_code, permission_name, kind, scope, module_key, parent_menu_key, sort_no, builtin, status)
SELECT 1, 'workbench:view', 'admin:workbench:view', '查看工作台', 'action', 'admin', 'workbench', 'workbench', 2, 1, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_iam_permission WHERE permission_code = 'workbench:view');

INSERT INTO qz_iam_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM qz_iam_role r
         JOIN qz_iam_permission p ON p.permission_code = 'workbench:view'
WHERE r.role_code = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM qz_iam_role_permission rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

INSERT INTO qz_sys_dict_type (tenant_id, dict_type, dict_name, status)
SELECT 1, 'banner_position', 'Banner 展示位', 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_sys_dict_type WHERE dict_type = 'banner_position');

INSERT INTO qz_sys_dict_item (tenant_id, dict_type, item_value, item_label, item_color, sort_no, status)
SELECT 1, 'banner_position', 'HOME_TOP', '首页顶部', 'primary', 1, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_sys_dict_item WHERE dict_type = 'banner_position' AND item_value = 'HOME_TOP');

INSERT INTO qz_sys_dict_item (tenant_id, dict_type, item_value, item_label, item_color, sort_no, status)
SELECT 1, 'banner_position', 'SIDEBAR', '侧边栏', 'info', 2, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_sys_dict_item WHERE dict_type = 'banner_position' AND item_value = 'SIDEBAR');

INSERT INTO qz_sys_dict_item (tenant_id, dict_type, item_value, item_label, item_color, sort_no, status)
SELECT 1, 'banner_position', 'POPUP', '弹窗', 'warning', 3, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM qz_sys_dict_item WHERE dict_type = 'banner_position' AND item_value = 'POPUP');

ALTER TABLE qz_kb_content
    ADD COLUMN current_version INT NOT NULL DEFAULT 0 AFTER min_level_code;

CREATE TABLE IF NOT EXISTS qz_kb_content_version (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    content_id      BIGINT       NOT NULL,
    version_no      INT          NOT NULL,
    title           VARCHAR(256) NOT NULL,
    summary         VARCHAR(512),
    body            LONGTEXT,
    min_level_code  VARCHAR(32),
    operator_id     BIGINT,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_content_version (content_id, version_no)
);
