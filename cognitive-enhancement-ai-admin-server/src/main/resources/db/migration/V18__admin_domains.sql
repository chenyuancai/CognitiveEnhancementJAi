-- 管理后台：会员/内容/运营 + 权限点扩展（对齐前端 admin:* 编码）

-- ==================== 权限点扩展字段 ====================
ALTER TABLE sys_permission
    ADD COLUMN scope VARCHAR(16) NOT NULL DEFAULT 'admin' COMMENT '作用域：admin/cog',
    ADD COLUMN kind VARCHAR(16) NOT NULL DEFAULT 'action' COMMENT '类型：menu/action',
    ADD COLUMN module_key VARCHAR(64) COMMENT '模块键',
    ADD COLUMN group_key VARCHAR(64) COMMENT '分组键',
    ADD COLUMN parent_menu_key VARCHAR(64) COMMENT '绑定菜单键',
    ADD COLUMN description VARCHAR(256) COMMENT '描述',
    ADD COLUMN builtin TINYINT NOT NULL DEFAULT 1 COMMENT '是否内置：1 是 0 否';

-- ==================== 会员表 ====================
CREATE TABLE IF NOT EXISTS biz_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    nickname VARCHAR(128) COMMENT '昵称快照',
    level VARCHAR(32) NOT NULL DEFAULT 'FREE' COMMENT '等级：FREE/PRO/ENTERPRISE',
    points BIGINT NOT NULL DEFAULT 0 COMMENT '积分',
    status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    expire_time DATETIME COMMENT '到期时间',
    tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user (user_id),
    INDEX idx_level (level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员';

-- ==================== 内容表 ====================
CREATE TABLE IF NOT EXISTS biz_content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title VARCHAR(256) NOT NULL COMMENT '标题',
    type VARCHAR(32) NOT NULL DEFAULT 'ARTICLE' COMMENT '类型：ARTICLE/NOTICE/FAQ',
    author VARCHAR(128) COMMENT '作者',
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '状态：DRAFT/PENDING/PUBLISHED/REJECTED/OFFLINE',
    summary VARCHAR(512) COMMENT '摘要',
    body MEDIUMTEXT COMMENT '正文',
    audit_remark VARCHAR(512) COMMENT '审核备注',
    tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容';

-- ==================== 运营 Banner 表 ====================
CREATE TABLE IF NOT EXISTS biz_banner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title VARCHAR(128) NOT NULL COMMENT '标题',
    image_url VARCHAR(512) NOT NULL COMMENT '图片URL',
    link_url VARCHAR(512) COMMENT '跳转链接',
    position VARCHAR(32) NOT NULL DEFAULT 'HOME_TOP' COMMENT '投放位置',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_position (position),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营Banner';

-- ==================== 对齐前端 admin:* 权限点 ====================
INSERT IGNORE INTO sys_permission (permission_code, permission_name, type, scope, kind, module_key, sort_no, builtin) VALUES
    ('admin:role:create',   '创建自定义角色', 'API', 'admin', 'action', 'account-governance', 110, 1),
    ('admin:role:update',   '编辑角色权限',   'API', 'admin', 'action', 'account-governance', 111, 1),
    ('admin:role:delete',   '删除角色',       'API', 'admin', 'action', 'account-governance', 112, 1),
    ('admin:permission:create', '新建自定义权限', 'API', 'admin', 'action', 'account-governance', 113, 1),
    ('admin:permission:update', '编辑自定义权限', 'API', 'admin', 'action', 'account-governance', 114, 1),
    ('admin:permission:delete', '删除自定义权限', 'API', 'admin', 'action', 'account-governance', 115, 1),
    ('admin:member:update', '调整会员等级',   'API', 'admin', 'action', 'member-system', 120, 1),
    ('admin:member:grant',  '手动授予会员',   'API', 'admin', 'action', 'member-system', 121, 1),
    ('admin:order:update',  '手动标记订单',   'API', 'admin', 'action', 'billing', 130, 1),
    ('admin:order:refund',  '发起退款',       'API', 'admin', 'action', 'billing', 131, 1),
    ('admin:content:update','编辑知识内容',   'API', 'admin', 'action', 'content', 140, 1),
    ('admin:content:audit', '审核内容',       'API', 'admin', 'action', 'content', 141, 1),
    ('admin:banner:create', '创建Banner',     'API', 'admin', 'action', 'operations', 150, 1),
    ('admin:banner:update', '编辑Banner',     'API', 'admin', 'action', 'operations', 151, 1),
    ('admin:banner:delete', '删除Banner',     'API', 'admin', 'action', 'operations', 152, 1);

-- 为 ADMIN 角色绑定全部权限点
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
CROSS JOIN sys_permission p
WHERE r.role_code = 'ADMIN';
