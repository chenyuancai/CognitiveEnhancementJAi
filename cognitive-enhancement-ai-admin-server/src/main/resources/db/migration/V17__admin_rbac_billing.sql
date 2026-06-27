-- 管理后台：RBAC 权限点 + 计费订单基础表
-- 对齐 common BaseEntity 的审计字段（tenant_code/create_by/update_by/deleted）

-- ==================== 补齐既有 RBAC 表审计字段 ====================
ALTER TABLE sys_role
    ADD COLUMN tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码',
    ADD COLUMN create_by BIGINT COMMENT '创建人',
    ADD COLUMN update_by BIGINT COMMENT '更新人',
    ADD COLUMN deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除';

ALTER TABLE sys_user
    ADD COLUMN create_by BIGINT COMMENT '创建人',
    ADD COLUMN update_by BIGINT COMMENT '更新人',
    ADD COLUMN deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除';

-- ==================== 权限点 / 菜单表 ====================
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    permission_code VARCHAR(128) NOT NULL UNIQUE COMMENT '权限点编码',
    permission_name VARCHAR(128) NOT NULL COMMENT '权限点名称',
    type VARCHAR(16) NOT NULL DEFAULT 'API' COMMENT '类型：MENU/BUTTON/API',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID，顶级为0',
    path VARCHAR(256) COMMENT '前端路由路径',
    component VARCHAR(256) COMMENT '前端组件',
    icon VARCHAR(64) COMMENT '图标',
    sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
    status VARCHAR(16) NOT NULL DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
    INDEX idx_parent (parent_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限点/菜单';

-- ==================== 角色-权限关联表 ====================
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限点ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- ==================== 订单表 ====================
CREATE TABLE IF NOT EXISTS biz_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '下单用户ID',
    package_id BIGINT COMMENT '套餐ID',
    package_name VARCHAR(128) COMMENT '套餐名称快照',
    amount BIGINT NOT NULL DEFAULT 0 COMMENT '订单金额（分）',
    currency VARCHAR(8) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PAID/REFUNDED/CANCELLED',
    pay_channel VARCHAR(32) COMMENT '支付渠道：MANUAL 等',
    pay_time DATETIME COMMENT '支付时间',
    refund_amount BIGINT COMMENT '退款金额（分）',
    refund_time DATETIME COMMENT '退款时间',
    remark VARCHAR(512) COMMENT '备注',
    tenant_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '租户编码',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

-- ==================== 初始化权限点 ====================
INSERT IGNORE INTO sys_permission (permission_code, permission_name, type, parent_id, sort_no) VALUES
    ('system:role:list',   '角色查询', 'API', 0, 10),
    ('system:role:create', '角色新增', 'API', 0, 11),
    ('system:role:update', '角色编辑', 'API', 0, 12),
    ('system:role:delete', '角色删除', 'API', 0, 13),
    ('system:role:assign', '角色授权', 'API', 0, 14),
    ('billing:order:list',   '订单查询',   'API', 0, 20),
    ('billing:order:pay',    '手动标记已付', 'API', 0, 21),
    ('billing:order:refund', '手动退款',   'API', 0, 22);
