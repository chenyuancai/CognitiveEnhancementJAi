-- 管理后台平台重建（v3）：不兼容旧 admin 表，统一 tenant_id + account 抽象
-- 研发阶段可清库；Center/Runtime 元数据表 tenant 迁移见 V21

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS biz_token_record;
DROP TABLE IF EXISTS biz_quota_member_alloc;
DROP TABLE IF EXISTS biz_quota_account;
DROP TABLE IF EXISTS biz_financial_record;
DROP TABLE IF EXISTS biz_subscription;
DROP TABLE IF EXISTS biz_order;
DROP TABLE IF EXISTS biz_quota_package;
DROP TABLE IF EXISTS biz_subscription_package;
DROP TABLE IF EXISTS biz_membership_change_log;
DROP TABLE IF EXISTS biz_account_membership;
DROP TABLE IF EXISTS biz_membership_level;
DROP TABLE IF EXISTS biz_org_member;
DROP TABLE IF EXISTS biz_org_department;
DROP TABLE IF EXISTS biz_organization;
DROP TABLE IF EXISTS biz_account;
DROP TABLE IF EXISTS biz_content_tag_rel;
DROP TABLE IF EXISTS biz_content_tag;
DROP TABLE IF EXISTS biz_knowledge_package_item;
DROP TABLE IF EXISTS biz_knowledge_package;
DROP TABLE IF EXISTS biz_content_import_job;
DROP TABLE IF EXISTS biz_content;
DROP TABLE IF EXISTS biz_announcement;
DROP TABLE IF EXISTS biz_banner;
DROP TABLE IF EXISTS biz_message_template;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_dict_item;
DROP TABLE IF EXISTS sys_dict_type;
DROP TABLE IF EXISTS sys_feature_switch;
DROP TABLE IF EXISTS sys_security_config;
DROP TABLE IF EXISTS sys_audit_log;
DROP TABLE IF EXISTS biz_member;

SET FOREIGN_KEY_CHECKS = 1;

-- ==================== 租户 ====================
CREATE TABLE sys_tenant (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_code     VARCHAR(64)  NOT NULL COMMENT '租户可读编码',
    tenant_name     VARCHAR(128) NOT NULL COMMENT '租户名称',
    segment         VARCHAR(8)   NOT NULL DEFAULT '2C' COMMENT '2C/2B/2G',
    status          VARCHAR(16)  NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED/DISABLED',
    create_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted         TINYINT      NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tenant_code (tenant_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户';

-- ==================== IAM ====================
CREATE TABLE sys_user (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 1 COMMENT '租户ID',
    username            VARCHAR(64)  NOT NULL COMMENT '登录名',
    password_hash       VARCHAR(128) NOT NULL COMMENT 'BCrypt',
    nickname            VARCHAR(128) COMMENT '昵称',
    email               VARCHAR(128) COMMENT '邮箱',
    phone               VARCHAR(32)  COMMENT '手机号',
    avatar_url          VARCHAR(512) COMMENT '头像',
    status              VARCHAR(16)  NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED/DISABLED/BANNED',
    ban_reason          VARCHAR(256) COMMENT '封禁原因',
    ban_until           DATETIME(3)  COMMENT '封禁截止',
    last_login_time     DATETIME(3)  COMMENT '最后登录',
    primary_account_id  BIGINT       COMMENT '默认账户',
    create_by           BIGINT,
    update_by           BIGINT,
    create_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted             TINYINT      NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_username (username),
    KEY idx_tenant (tenant_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE sys_role (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    role_code    VARCHAR(64)  NOT NULL COMMENT '角色编码',
    role_name    VARCHAR(128) NOT NULL,
    description  VARCHAR(256),
    status       VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    builtin      TINYINT      NOT NULL DEFAULT 0,
    avatar_color VARCHAR(32),
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色';

CREATE TABLE sys_permission (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        BIGINT       NOT NULL DEFAULT 1,
    permission_code  VARCHAR(128) NOT NULL COMMENT '规范码 iam:role:update',
    alias_code       VARCHAR(128) COMMENT '前端码 admin:role:update',
    permission_name  VARCHAR(128) NOT NULL,
    kind             VARCHAR(16)  NOT NULL DEFAULT 'action' COMMENT 'menu/action',
    scope            VARCHAR(16)  NOT NULL DEFAULT 'admin' COMMENT 'admin/cog',
    module_key       VARCHAR(64),
    group_key        VARCHAR(64),
    parent_menu_key  VARCHAR(64),
    parent_id        BIGINT       NOT NULL DEFAULT 0,
    path             VARCHAR(256),
    component        VARCHAR(256),
    icon             VARCHAR(64),
    sort_no          INT          NOT NULL DEFAULT 0,
    description      VARCHAR(256),
    builtin          TINYINT      NOT NULL DEFAULT 1,
    status           VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by        BIGINT,
    update_by        BIGINT,
    create_time      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted          TINYINT      NOT NULL DEFAULT 0,
    version          INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_permission_code (permission_code),
    KEY idx_alias (alias_code),
    KEY idx_scope_kind (scope, kind)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限/菜单';

CREATE TABLE sys_role_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限';

-- ==================== 账户 & 组织 ====================
CREATE TABLE biz_account (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    account_type    VARCHAR(16)  NOT NULL COMMENT 'INDIVIDUAL/ENTERPRISE/GOVERNMENT',
    segment         VARCHAR(8)   NOT NULL COMMENT '2C/2B/2G',
    display_name    VARCHAR(128) NOT NULL,
    owner_user_id   BIGINT       NOT NULL,
    status          VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by       BIGINT,
    update_by       BIGINT,
    create_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted         TINYINT      NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    KEY idx_tenant (tenant_id),
    KEY idx_owner (owner_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商业账户';

CREATE TABLE biz_organization (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id           BIGINT       NOT NULL,
    tenant_id            BIGINT       NOT NULL,
    org_type             VARCHAR(16)  NOT NULL COMMENT 'ENTERPRISE/GOVERNMENT',
    org_name             VARCHAR(128) NOT NULL,
    unified_social_code  VARCHAR(64),
    seat_limit           INT          NOT NULL DEFAULT 10,
    contact_name         VARCHAR(64),
    contact_phone        VARCHAR(32),
    create_by            BIGINT,
    update_by            BIGINT,
    create_time          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted              TINYINT      NOT NULL DEFAULT 0,
    version              INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_account (account_id),
    UNIQUE KEY uk_org_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织';

CREATE TABLE biz_org_department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    org_id      BIGINT       NOT NULL,
    parent_id   BIGINT       NOT NULL DEFAULT 0,
    dept_name   VARCHAR(128) NOT NULL,
    sort_no     INT          NOT NULL DEFAULT 0,
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    KEY idx_org (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门';

CREATE TABLE biz_org_member (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    org_id      BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    dept_id     BIGINT,
    org_role    VARCHAR(32)  NOT NULL DEFAULT 'MEMBER' COMMENT 'OWNER/ADMIN/MEMBER',
    status      VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    joined_at   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_org_user (org_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织成员';

-- ==================== 会员 ====================
CREATE TABLE biz_membership_level (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    level_code    VARCHAR(32)  NOT NULL,
    level_name    VARCHAR(64)  NOT NULL,
    segment       VARCHAR(8)   NOT NULL DEFAULT '2C' COMMENT '2C/2B/2G/ALL',
    is_default    TINYINT      NOT NULL DEFAULT 0,
    sort_no       INT          NOT NULL DEFAULT 0,
    status        VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    benefits_json JSON,
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_level_code (level_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级目录';

CREATE TABLE biz_account_membership (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    account_id  BIGINT       NOT NULL,
    level_id    BIGINT       NOT NULL,
    level_code  VARCHAR(32)  NOT NULL,
    expire_at   DATETIME(3),
    source      VARCHAR(32)  NOT NULL DEFAULT 'DEFAULT' COMMENT 'SUBSCRIPTION/GRANT/DEFAULT',
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户会员态';

CREATE TABLE biz_membership_change_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    account_id      BIGINT       NOT NULL,
    user_id         BIGINT,
    from_level_code VARCHAR(32),
    to_level_code   VARCHAR(32)  NOT NULL,
    change_type     VARCHAR(32)  NOT NULL,
    order_id        BIGINT,
    operator_id     BIGINT,
    remark          VARCHAR(512),
    create_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员变更审计';

-- ==================== 计费 ====================
CREATE TABLE biz_subscription_package (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL DEFAULT 1,
    package_code      VARCHAR(64)  NOT NULL,
    package_name      VARCHAR(128) NOT NULL,
    segment           VARCHAR(8)   NOT NULL DEFAULT '2C',
    level_id          BIGINT       NOT NULL,
    billing_period    VARCHAR(16)  NOT NULL COMMENT 'MONTH/QUARTER/YEAR',
    period_count      INT          NOT NULL DEFAULT 1,
    price_fen         BIGINT       NOT NULL DEFAULT 0,
    original_price_fen BIGINT,
    cycle_token_quota BIGINT       NOT NULL DEFAULT 0,
    seat_count        INT          NOT NULL DEFAULT 1,
    sale_mode         VARCHAR(16)  NOT NULL DEFAULT 'SELF_SERVICE',
    require_contract  TINYINT      NOT NULL DEFAULT 0,
    status            VARCHAR(16)  NOT NULL DEFAULT 'ON_SALE',
    snapshot_json     JSON,
    create_by         BIGINT,
    update_by         BIGINT,
    create_time       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted           TINYINT      NOT NULL DEFAULT 0,
    version           INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_pkg_code (package_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅套餐';

CREATE TABLE biz_quota_package (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    package_code  VARCHAR(64)  NOT NULL,
    package_name  VARCHAR(128) NOT NULL,
    segment       VARCHAR(8)   NOT NULL DEFAULT '2C',
    token_amount  BIGINT       NOT NULL DEFAULT 0,
    price_fen     BIGINT       NOT NULL DEFAULT 0,
    valid_days    INT          NOT NULL DEFAULT 0,
    status        VARCHAR(16)  NOT NULL DEFAULT 'ON_SALE',
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_quota_pkg_code (package_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='额度加油包';

CREATE TABLE biz_order (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id              BIGINT       NOT NULL,
    order_no               VARCHAR(64)  NOT NULL,
    account_id             BIGINT       NOT NULL,
    buyer_user_id          BIGINT       NOT NULL,
    order_type             VARCHAR(16)  NOT NULL COMMENT 'SUBSCRIPTION/QUOTA',
    package_id             BIGINT,
    package_snapshot_json  JSON,
    amount_fen             BIGINT       NOT NULL DEFAULT 0,
    currency               VARCHAR(8)   NOT NULL DEFAULT 'CNY',
    status                 VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    pay_channel            VARCHAR(32),
    pay_time               DATETIME(3),
    fulfill_time           DATETIME(3),
    idempotency_key        VARCHAR(64),
    refund_amount_fen      BIGINT,
    refund_time            DATETIME(3),
    remark                 VARCHAR(512),
    create_by              BIGINT,
    update_by              BIGINT,
    create_time            DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time            DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted                TINYINT      NOT NULL DEFAULT 0,
    version                INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    UNIQUE KEY uk_idempotency (idempotency_key),
    KEY idx_account (account_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

CREATE TABLE biz_subscription (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id             BIGINT       NOT NULL,
    account_id            BIGINT       NOT NULL,
    order_id              BIGINT,
    package_id            BIGINT,
    level_code            VARCHAR(32)  NOT NULL,
    status                VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    start_at              DATETIME(3)  NOT NULL,
    end_at                DATETIME(3)  NOT NULL,
    auto_renew            TINYINT      NOT NULL DEFAULT 0,
    package_snapshot_json JSON,
    create_by             BIGINT,
    update_by             BIGINT,
    create_time           DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time           DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted               TINYINT      NOT NULL DEFAULT 0,
    version               INT          NOT NULL DEFAULT 0,
    KEY idx_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅';

CREATE TABLE biz_financial_record (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        BIGINT       NOT NULL,
    account_id       BIGINT       NOT NULL,
    order_id         BIGINT,
    record_type      VARCHAR(32)  NOT NULL,
    amount_fen       BIGINT       NOT NULL,
    balance_after_fen BIGINT,
    remark           VARCHAR(512),
    create_time      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金流水';

-- ==================== 额度 ====================
CREATE TABLE biz_quota_account (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    account_id      BIGINT       NOT NULL,
    cycle_remaining BIGINT       NOT NULL DEFAULT 0,
    cycle_total     BIGINT       NOT NULL DEFAULT 0,
    cycle_reset_at  DATETIME(3),
    gift_remaining  BIGINT       NOT NULL DEFAULT 0,
    gift_total      BIGINT       NOT NULL DEFAULT 0,
    topup_remaining BIGINT       NOT NULL DEFAULT 0,
    topup_total     BIGINT       NOT NULL DEFAULT 0,
    create_by       BIGINT,
    update_by       BIGINT,
    create_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted         TINYINT      NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_quota_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='额度账户';

CREATE TABLE biz_token_record (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        BIGINT       NOT NULL,
    account_id       BIGINT       NOT NULL,
    member_user_id   BIGINT,
    record_type      VARCHAR(32)  NOT NULL,
    bucket           VARCHAR(16)  NOT NULL COMMENT 'CYCLE/GIFT/TOPUP',
    delta_amount     BIGINT       NOT NULL,
    balance_after    BIGINT       NOT NULL,
    biz_type         VARCHAR(32),
    biz_id           VARCHAR(64),
    idempotency_key  VARCHAR(64),
    remark           VARCHAR(512),
    create_time      DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_token_idem (idempotency_key),
    KEY idx_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token流水';

CREATE TABLE biz_quota_member_alloc (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL,
    account_id        BIGINT       NOT NULL,
    user_id           BIGINT       NOT NULL,
    allocated_amount  BIGINT       NOT NULL DEFAULT 0,
    used_amount       BIGINT       NOT NULL DEFAULT 0,
    create_by         BIGINT,
    update_by         BIGINT,
    create_time       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted           TINYINT      NOT NULL DEFAULT 0,
    version           INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_alloc (account_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成员额度分配';

-- ==================== 内容 ====================
CREATE TABLE biz_content (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    title        VARCHAR(256) NOT NULL,
    content_type VARCHAR(32)  NOT NULL DEFAULT 'ARTICLE',
    author       VARCHAR(128),
    status       VARCHAR(16)  NOT NULL DEFAULT 'DRAFT',
    summary      VARCHAR(512),
    body         MEDIUMTEXT,
    audit_remark VARCHAR(512),
    published_at DATETIME(3),
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容';

CREATE TABLE biz_content_tag (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    tag_name    VARCHAR(64)  NOT NULL,
    tag_color   VARCHAR(32),
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tag (tenant_id, tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签';

CREATE TABLE biz_content_tag_rel (
    content_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (content_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关联';

CREATE TABLE biz_knowledge_package (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    package_name VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    status      VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识包';

CREATE TABLE biz_knowledge_package_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id  BIGINT NOT NULL,
    parent_id   BIGINT NOT NULL DEFAULT 0,
    content_id  BIGINT,
    title       VARCHAR(256),
    sort_no     INT    NOT NULL DEFAULT 0,
    create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_package (package_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识包条目';

CREATE TABLE biz_content_import_job (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    file_name    VARCHAR(256) NOT NULL,
    file_url     VARCHAR(512),
    status       VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    total_count  INT          NOT NULL DEFAULT 0,
    success_count INT         NOT NULL DEFAULT 0,
    fail_count   INT          NOT NULL DEFAULT 0,
    result_json  JSON,
    create_by    BIGINT,
    create_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容导入任务';

-- ==================== 运营 ====================
CREATE TABLE biz_banner (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    title       VARCHAR(128) NOT NULL,
    image_url   VARCHAR(512) NOT NULL,
    link_url    VARCHAR(512),
    position    VARCHAR(32)  NOT NULL DEFAULT 'HOME_TOP',
    sort_no     INT          NOT NULL DEFAULT 0,
    status      VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    start_time  DATETIME(3),
    end_time    DATETIME(3),
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner';

CREATE TABLE biz_announcement (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    title       VARCHAR(256) NOT NULL,
    body        MEDIUMTEXT,
    status      VARCHAR(16)  NOT NULL DEFAULT 'DRAFT',
    publish_at  DATETIME(3),
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告';

CREATE TABLE biz_message_template (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    template_code VARCHAR(64)  NOT NULL,
    template_name VARCHAR(128) NOT NULL,
    channel       VARCHAR(16)  NOT NULL COMMENT 'SMS/EMAIL/IN_APP',
    content       TEXT         NOT NULL,
    variable_schema JSON,
    status        VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tpl_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板';

-- ==================== 系统 ====================
CREATE TABLE sys_dict_type (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    dict_type   VARCHAR(64)  NOT NULL,
    dict_name   VARCHAR(128) NOT NULL,
    status      VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    remark      VARCHAR(256),
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型';

CREATE TABLE sys_dict_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    dict_type   VARCHAR(64)  NOT NULL,
    item_value  VARCHAR(64)  NOT NULL,
    item_label  VARCHAR(128) NOT NULL,
    item_color  VARCHAR(32),
    sort_no     INT          NOT NULL DEFAULT 0,
    status      VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by   BIGINT,
    update_by   BIGINT,
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_dict_item (dict_type, item_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项';

CREATE TABLE sys_feature_switch (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    feature_key  VARCHAR(64)  NOT NULL,
    feature_name VARCHAR(128) NOT NULL,
    segment      VARCHAR(8)   NOT NULL DEFAULT 'ALL',
    enabled      TINYINT      NOT NULL DEFAULT 0,
    gray_rule_json JSON,
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_feature (feature_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Feature开关';

CREATE TABLE sys_security_config (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    config_key   VARCHAR(64)  NOT NULL,
    config_value TEXT         NOT NULL,
    description  VARCHAR(256),
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sec_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全配置';

CREATE TABLE sys_audit_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    operator_id   BIGINT,
    operator_name VARCHAR(128),
    action        VARCHAR(64)  NOT NULL,
    resource_type VARCHAR(64)  NOT NULL,
    resource_id   VARCHAR(64),
    before_json   JSON,
    after_json    JSON,
    ip_address    VARCHAR(64),
    create_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_resource (resource_type, resource_id),
    KEY idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理后台审计';

-- ==================== 种子数据 ====================
INSERT INTO sys_tenant (id, tenant_code, tenant_name, segment, status) VALUES
    (1, 'platform', '启知平台', '2C', 'ENABLED');

INSERT INTO sys_role (id, tenant_id, role_code, role_name, description, status, builtin, avatar_color) VALUES
    (1, 1, 'ADMIN', '超级管理员', '拥有全部权限', 'ENABLED', 1, '#6366f1'),
    (2, 1, 'OPERATOR', '运营', '会员计费运营', 'ENABLED', 1, '#10b981'),
    (3, 1, 'CONTENT', '内容', '内容管理', 'ENABLED', 1, '#f59e0b'),
    (4, 1, 'SUPPORT', '客服', '只读+工单', 'ENABLED', 1, '#3b82f6');

INSERT INTO sys_user (id, tenant_id, username, password_hash, nickname, status, primary_account_id) VALUES
    (1, 1, 'admin', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '管理员', 'ENABLED', 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO biz_account (id, tenant_id, account_type, segment, display_name, owner_user_id, status) VALUES
    (1, 1, 'INDIVIDUAL', '2C', '管理员', 1, 'ENABLED');

UPDATE sys_user SET primary_account_id = 1 WHERE id = 1;

INSERT INTO biz_membership_level (id, tenant_id, level_code, level_name, segment, is_default, sort_no, benefits_json) VALUES
    (1, 1, 'FREE', '免费版', '2C', 1, 10, '{"monthlyTokenK":100}'),
    (2, 1, 'PRO', '专业版', '2C', 0, 20, '{"monthlyTokenK":1000}'),
    (3, 1, 'ENTERPRISE', '企业版', '2B', 0, 30, '{"monthlyTokenK":10000}');

INSERT INTO biz_account_membership (tenant_id, account_id, level_id, level_code, source) VALUES
    (1, 1, 1, 'FREE', 'DEFAULT');

INSERT INTO biz_quota_account (tenant_id, account_id, cycle_remaining, cycle_total, gift_remaining, topup_remaining) VALUES
    (1, 1, 100000, 100000, 0, 0);

-- 权限点（规范码 + 前端 alias）
INSERT INTO sys_permission (permission_code, alias_code, permission_name, kind, scope, module_key, parent_menu_key, path, sort_no, builtin) VALUES
    ('iam:user:read',       'admin:user:view',       '查看用户',       'action', 'admin', 'account-governance', 'users', NULL, 10, 1),
    ('iam:user:update',     'admin:user:update',     '编辑用户',       'action', 'admin', 'account-governance', 'users', NULL, 11, 1),
    ('iam:user:export',     'admin:user:export',     '导出用户',       'action', 'admin', 'account-governance', 'users', NULL, 12, 1),
    ('iam:role:update',     'admin:role:update',     '编辑角色权限',   'action', 'admin', 'account-governance', 'roles', NULL, 20, 1),
    ('iam:role:create',     'admin:role:create',     '创建角色',       'action', 'admin', 'account-governance', 'roles', NULL, 21, 1),
    ('iam:role:delete',     'admin:role:delete',     '删除角色',       'action', 'admin', 'account-governance', 'roles', NULL, 22, 1),
    ('iam:permission:create','admin:permission:create','新建权限',     'action', 'admin', 'account-governance', 'permissions', NULL, 30, 1),
    ('iam:permission:update','admin:permission:update','编辑权限',     'action', 'admin', 'account-governance', 'permissions', NULL, 31, 1),
    ('iam:permission:delete','admin:permission:delete','删除权限',     'action', 'admin', 'account-governance', 'permissions', NULL, 32, 1),
    ('membership:level:update','admin:member:update', '调整会员等级',   'action', 'admin', 'member-system', 'member-levels', NULL, 40, 1),
    ('membership:level:grant', 'admin:member:grant',  '手动授予会员',   'action', 'admin', 'member-system', 'member-levels', NULL, 41, 1),
    ('billing:order:update',   'admin:order:update',  '手动标记订单',   'action', 'admin', 'billing', 'orders', NULL, 50, 1),
    ('billing:order:refund',   'admin:order:refund',  '发起退款',       'action', 'admin', 'billing', 'orders', NULL, 51, 1),
    ('billing:order:export',   'admin:order:export',  '导出订单',       'action', 'admin', 'billing', 'orders', NULL, 52, 1),
    ('content:item:update',    'admin:content:update','编辑内容',       'action', 'admin', 'content', 'content-items', NULL, 60, 1),
    ('content:item:audit',     'admin:content:audit', '审核内容',       'action', 'admin', 'content', 'content-review', NULL, 61, 1),
    ('ops:banner:create',      'admin:banner:create', '创建Banner',     'action', 'admin', 'operations', 'banners', NULL, 70, 1),
    ('ops:banner:update',      'admin:banner:update', '编辑Banner',     'action', 'admin', 'operations', 'banners', NULL, 71, 1),
    ('ops:banner:delete',      'admin:banner:delete', '删除Banner',     'action', 'admin', 'operations', 'banners', NULL, 72, 1),
    ('system:security:update', 'admin:security:update','修改安全配置',  'action', 'admin', 'system', 'security', NULL, 80, 1),
    ('system:dict:read',       'admin:dict:read',     '查看字典',       'action', 'admin', 'system', 'dicts', NULL, 90, 1),
    ('system:dict:update',     'admin:dict:update',   '编辑字典',       'action', 'admin', 'system', 'dicts', NULL, 91, 1);

-- 菜单项
INSERT INTO sys_permission (permission_code, alias_code, permission_name, kind, scope, module_key, parent_id, path, icon, sort_no, builtin) VALUES
    ('menu:workbench', NULL, '工作台', 'menu', 'admin', 'workbench', 0, '/cms/dashboard', 'Odometer', 1, 1),
    ('menu:users', NULL, '用户管理', 'menu', 'admin', 'account-governance', 0, '/cms/users', 'User', 10, 1),
    ('menu:roles', NULL, '角色管理', 'menu', 'admin', 'account-governance', 0, '/cms/roles', 'UserFilled', 11, 1),
    ('menu:orders', NULL, '订单管理', 'menu', 'admin', 'billing', 0, '/cms/orders', 'Tickets', 20, 1),
    ('menu:dicts', NULL, '字典枚举', 'menu', 'admin', 'system', 0, '/cms/dicts', 'Collection', 90, 1);

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 字典种子
INSERT INTO sys_dict_type (dict_type, dict_name) VALUES
    ('order_status', '订单状态'),
    ('account_type', '账户类型'),
    ('user_status', '用户状态');

INSERT INTO sys_dict_item (dict_type, item_value, item_label, item_color, sort_no) VALUES
    ('order_status', 'PENDING', '待支付', 'warning', 1),
    ('order_status', 'PAID', '已支付', 'success', 2),
    ('order_status', 'FULFILLED', '已发放', 'success', 3),
    ('order_status', 'REFUNDED', '已退款', 'info', 4),
    ('order_status', 'CANCELLED', '已取消', 'default', 5),
    ('account_type', 'INDIVIDUAL', '个人', 'primary', 1),
    ('account_type', 'ENTERPRISE', '企业', 'success', 2),
    ('account_type', 'GOVERNMENT', '政府', 'warning', 3),
    ('user_status', 'ENABLED', '正常', 'success', 1),
    ('user_status', 'DISABLED', '禁用', 'warning', 2),
    ('user_status', 'BANNED', '封禁', 'danger', 3);
