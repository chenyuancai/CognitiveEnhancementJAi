-- Admin 集成测试最小 schema（H2 MySQL 模式）

CREATE TABLE IF NOT EXISTS qz_iam_tenant (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_code  VARCHAR(64)  NOT NULL,
    tenant_name  VARCHAR(128) NOT NULL,
    segment      VARCHAR(8)   NOT NULL DEFAULT '2C',
    status       VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_iam_user (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id          BIGINT       NOT NULL DEFAULT 1,
    username           VARCHAR(64)  NOT NULL,
    password_hash      VARCHAR(128) NOT NULL,
    nickname           VARCHAR(128),
    email              VARCHAR(128),
    phone              VARCHAR(32),
    avatar_url         VARCHAR(512),
    status             VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    user_type          VARCHAR(16)  NOT NULL DEFAULT 'CUSTOMER',
    ban_reason         VARCHAR(256),
    ban_until          TIMESTAMP,
    last_login_time    TIMESTAMP,
    primary_account_id BIGINT,
    create_by          BIGINT,
    update_by          BIGINT,
    create_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted            TINYINT      NOT NULL DEFAULT 0,
    version            INT          NOT NULL DEFAULT 0,
    UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS qz_iam_role (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    role_code    VARCHAR(64)  NOT NULL,
    role_name    VARCHAR(128) NOT NULL,
    description  VARCHAR(256),
    status       VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    builtin      TINYINT      NOT NULL DEFAULT 0,
    avatar_color VARCHAR(32),
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    UNIQUE (role_code)
);

CREATE TABLE IF NOT EXISTS qz_iam_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS qz_iam_permission (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        BIGINT       NOT NULL DEFAULT 1,
    permission_code  VARCHAR(128) NOT NULL,
    alias_code       VARCHAR(128),
    permission_name  VARCHAR(128) NOT NULL,
    kind             VARCHAR(16)  NOT NULL DEFAULT 'action',
    scope            VARCHAR(16)  NOT NULL DEFAULT 'admin',
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
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          TINYINT      NOT NULL DEFAULT 0,
    version          INT          NOT NULL DEFAULT 0,
    UNIQUE (permission_code)
);

CREATE TABLE IF NOT EXISTS qz_iam_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS qz_acct_account (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL,
    account_type  VARCHAR(16)  NOT NULL,
    segment       VARCHAR(8)   NOT NULL,
    display_name  VARCHAR(128) NOT NULL,
    owner_user_id BIGINT       NOT NULL,
    status        VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_acct_org (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id          BIGINT       NOT NULL,
    tenant_id           BIGINT       NOT NULL,
    org_type            VARCHAR(16)  NOT NULL,
    org_name            VARCHAR(128) NOT NULL,
    unified_social_code VARCHAR(64),
    seat_limit          INT          NOT NULL DEFAULT 10,
    contact_name        VARCHAR(64),
    contact_phone       VARCHAR(32),
    create_by           BIGINT,
    update_by           BIGINT,
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             TINYINT      NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS qz_mbr_level (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    level_code    VARCHAR(32)  NOT NULL,
    level_name    VARCHAR(64)  NOT NULL,
    segment       VARCHAR(8)   NOT NULL DEFAULT '2C',
    is_default    TINYINT      NOT NULL DEFAULT 0,
    sort_no       INT          NOT NULL DEFAULT 0,
    status        VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    benefits_json VARCHAR(1024),
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0
);

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
    UNIQUE (tenant_id, benefit_code)
);

CREATE TABLE IF NOT EXISTS qz_mbr_level_benefit (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    level_id      BIGINT       NOT NULL,
    benefit_code  VARCHAR(64)  NOT NULL,
    benefit_value VARCHAR(128) NOT NULL,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (level_id, benefit_code)
);

CREATE TABLE IF NOT EXISTS qz_mbr_account (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    account_id  BIGINT       NOT NULL,
    level_id    BIGINT       NOT NULL,
    level_code  VARCHAR(32)  NOT NULL,
    expire_at   TIMESTAMP,
    source      VARCHAR(32)  NOT NULL DEFAULT 'DEFAULT',
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS qz_mbr_membership_change_log (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    account_id      BIGINT       NOT NULL,
    user_id         BIGINT,
    from_level_code VARCHAR(32),
    to_level_code   VARCHAR(32)  NOT NULL,
    change_type     VARCHAR(32)  NOT NULL,
    message         VARCHAR(512),
    order_id        BIGINT,
    operator_id     BIGINT,
    remark          VARCHAR(512),
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qz_bill_quota_package (
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
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0,
    UNIQUE (package_code)
);

CREATE TABLE IF NOT EXISTS qz_bill_subscription_package (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id          BIGINT       NOT NULL DEFAULT 1,
    package_code       VARCHAR(64)  NOT NULL,
    package_name       VARCHAR(128) NOT NULL,
    segment            VARCHAR(8)   NOT NULL DEFAULT '2C',
    level_id           BIGINT       NOT NULL,
    billing_period     VARCHAR(16)  NOT NULL,
    period_count       INT          NOT NULL DEFAULT 1,
    trial_days         INT          NOT NULL DEFAULT 0,
    price_fen          BIGINT       NOT NULL DEFAULT 0,
    original_price_fen BIGINT,
    cycle_token_quota  BIGINT       NOT NULL DEFAULT 0,
    daily_limit        BIGINT       NOT NULL DEFAULT 0,
    concurrent_limit   INT          NOT NULL DEFAULT 0,
    model_scope        VARCHAR(256),
    seat_count         INT          NOT NULL DEFAULT 1,
    sale_mode          VARCHAR(16)  NOT NULL DEFAULT 'SELF_SERVICE',
    require_contract   TINYINT      NOT NULL DEFAULT 0,
    status             VARCHAR(16)  NOT NULL DEFAULT 'ON_SALE',
    snapshot_json      VARCHAR(2048),
    create_by          BIGINT,
    update_by          BIGINT,
    create_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted            TINYINT      NOT NULL DEFAULT 0,
    version            INT          NOT NULL DEFAULT 0,
    UNIQUE (package_code)
);

CREATE TABLE IF NOT EXISTS qz_bill_order (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id             BIGINT       NOT NULL,
    order_no              VARCHAR(64)  NOT NULL,
    account_id            BIGINT       NOT NULL,
    buyer_user_id         BIGINT       NOT NULL,
    order_type            VARCHAR(16)  NOT NULL,
    package_id            BIGINT,
    package_snapshot_json VARCHAR(2048),
    amount_fen            BIGINT       NOT NULL DEFAULT 0,
    currency              VARCHAR(8)   NOT NULL DEFAULT 'CNY',
    status                VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    pay_channel           VARCHAR(32),
    pay_time              TIMESTAMP,
    fulfill_time          TIMESTAMP,
    idempotency_key       VARCHAR(64),
    refund_amount_fen     BIGINT,
    refund_time           TIMESTAMP,
    remark                VARCHAR(512),
    create_by             BIGINT,
    update_by             BIGINT,
    create_time           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted               TINYINT      NOT NULL DEFAULT 0,
    version               INT          NOT NULL DEFAULT 0,
    UNIQUE (order_no)
);

CREATE TABLE IF NOT EXISTS qz_bill_subscription (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id             BIGINT       NOT NULL,
    account_id            BIGINT       NOT NULL,
    order_id              BIGINT,
    package_id            BIGINT,
    level_code            VARCHAR(32)  NOT NULL,
    status                VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    phase                 VARCHAR(16)  NOT NULL DEFAULT 'FORMAL',
    start_at              TIMESTAMP    NOT NULL,
    end_at                TIMESTAMP    NOT NULL,
    auto_renew            TINYINT      NOT NULL DEFAULT 0,
    package_snapshot_json VARCHAR(2048),
    create_by             BIGINT,
    update_by             BIGINT,
    create_time           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted               TINYINT      NOT NULL DEFAULT 0,
    version               INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_bill_financial_record (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL,
    account_id        BIGINT       NOT NULL,
    order_id          BIGINT,
    record_type       VARCHAR(32)  NOT NULL,
    message           VARCHAR(512),
    amount_fen        BIGINT       NOT NULL,
    balance_after_fen BIGINT,
    remark            VARCHAR(512),
    create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qz_mbr_quota_account (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    account_id      BIGINT       NOT NULL,
    cycle_remaining BIGINT       NOT NULL DEFAULT 0,
    cycle_total     BIGINT       NOT NULL DEFAULT 0,
    cycle_reset_at  TIMESTAMP,
    gift_remaining  BIGINT       NOT NULL DEFAULT 0,
    gift_total      BIGINT       NOT NULL DEFAULT 0,
    topup_remaining BIGINT       NOT NULL DEFAULT 0,
    topup_total     BIGINT       NOT NULL DEFAULT 0,
    create_by       BIGINT,
    update_by       BIGINT,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS qz_mbr_token_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    account_id      BIGINT       NOT NULL,
    member_user_id  BIGINT,
    record_type     VARCHAR(32)  NOT NULL,
    bucket          VARCHAR(16)  NOT NULL,
    delta_amount    BIGINT       NOT NULL,
    balance_after   BIGINT       NOT NULL,
    biz_type        VARCHAR(32),
    biz_id          VARCHAR(64),
    idempotency_key VARCHAR(64),
    message         VARCHAR(512),
    remark          VARCHAR(512),
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (idempotency_key)
);

CREATE TABLE IF NOT EXISTS qz_mbr_quota_member_alloc (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        BIGINT       NOT NULL,
    account_id       BIGINT       NOT NULL,
    user_id          BIGINT       NOT NULL,
    allocated_amount BIGINT       NOT NULL DEFAULT 0,
    used_amount      BIGINT       NOT NULL DEFAULT 0,
    create_by        BIGINT,
    update_by        BIGINT,
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          TINYINT      NOT NULL DEFAULT 0,
    version          INT          NOT NULL DEFAULT 0,
    UNIQUE (account_id, user_id)
);

CREATE TABLE IF NOT EXISTS qz_kb_content (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    title        VARCHAR(256) NOT NULL,
    content_type VARCHAR(32)  NOT NULL DEFAULT 'ARTICLE',
    author       VARCHAR(128),
    status       VARCHAR(16)  NOT NULL DEFAULT 'DRAFT',
    summary      VARCHAR(512),
    body         CLOB,
    audit_remark VARCHAR(512),
    min_level_code VARCHAR(32),
    current_version INT NOT NULL DEFAULT 0,
    published_at TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_kb_content_version (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    content_id      BIGINT       NOT NULL,
    version_no      INT          NOT NULL,
    title           VARCHAR(256) NOT NULL,
    summary         VARCHAR(512),
    body            CLOB,
    min_level_code  VARCHAR(32),
    operator_id     BIGINT,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (content_id, version_no)
);

CREATE TABLE IF NOT EXISTS qz_kb_content_import_job (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    file_name     VARCHAR(256) NOT NULL,
    file_url      VARCHAR(512),
    source_content CLOB,
    status        VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    total_count   INT          NOT NULL DEFAULT 0,
    success_count INT          NOT NULL DEFAULT 0,
    fail_count    INT          NOT NULL DEFAULT 0,
    result_json   VARCHAR(4096),
    create_by     BIGINT,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qz_ops_banner (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    title       VARCHAR(128) NOT NULL,
    image_url   VARCHAR(512) NOT NULL,
    link_url    VARCHAR(512),
    position    VARCHAR(32)  NOT NULL DEFAULT 'HOME_TOP',
    sort_no     INT          NOT NULL DEFAULT 0,
    status      VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    start_time  TIMESTAMP,
    end_time    TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_kb_content_tag (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    tag_name    VARCHAR(64)  NOT NULL,
    tag_color   VARCHAR(32),
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, tag_name)
);

CREATE TABLE IF NOT EXISTS qz_kb_content_tag_rel (
    content_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (content_id, tag_id)
);

CREATE TABLE IF NOT EXISTS qz_kb_content_chunk (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL,
    content_id   BIGINT       NOT NULL,
    task_code    VARCHAR(64)  NULL,
    chunk_index  INT          NOT NULL,
    heading_path VARCHAR(512) NULL,
    chunk_text   CLOB         NOT NULL,
    token_est    INT          NULL,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qz_kb_vector_index_record (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL,
    content_id   BIGINT       NOT NULL,
    chunk_id     BIGINT       NOT NULL,
    model_code   VARCHAR(128) NULL,
    dim          INT          NOT NULL,
    vector_json  CLOB         NOT NULL,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qz_kb_knowledge_package (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    package_name VARCHAR(128) NOT NULL,
    description  VARCHAR(512),
    status       VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_kb_knowledge_package_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    package_id  BIGINT NOT NULL,
    parent_id   BIGINT NOT NULL DEFAULT 0,
    content_id  BIGINT,
    title       VARCHAR(256),
    sort_no     INT    NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS qz_ops_announcement (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    title       VARCHAR(256) NOT NULL,
    body        CLOB,
    status      VARCHAR(16)  NOT NULL DEFAULT 'DRAFT',
    publish_at  TIMESTAMP,
    target_level_codes VARCHAR(512),
    target_user_ids VARCHAR(2048),
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_ops_message_template (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 1,
    template_code   VARCHAR(64)  NOT NULL,
    template_name   VARCHAR(128) NOT NULL,
    channel         VARCHAR(16)  NOT NULL,
    content         CLOB         NOT NULL,
    variable_schema VARCHAR(4096),
    status          VARCHAR(16)  NOT NULL DEFAULT 'ENABLED',
    create_by       BIGINT,
    update_by       BIGINT,
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    version         INT          NOT NULL DEFAULT 0,
    UNIQUE (template_code)
);

CREATE TABLE IF NOT EXISTS qz_ops_support_ticket (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 1,
    ticket_no           VARCHAR(32)  NOT NULL,
    title               VARCHAR(256) NOT NULL,
    body                CLOB,
    category            VARCHAR(64),
    status              VARCHAR(16)  NOT NULL DEFAULT 'OPEN',
    priority            VARCHAR(16)  NOT NULL DEFAULT 'NORMAL',
    submitter_user_id   BIGINT,
    assignee_user_id    BIGINT,
    resolved_at         TIMESTAMP,
    create_by           BIGINT,
    update_by           BIGINT,
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             TINYINT      NOT NULL DEFAULT 0,
    version             INT          NOT NULL DEFAULT 0,
    UNIQUE (ticket_no)
);

CREATE TABLE IF NOT EXISTS qz_ops_in_app_message (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    user_id       BIGINT       NOT NULL,
    template_code VARCHAR(64),
    title         VARCHAR(128),
    content       CLOB         NOT NULL,
    read_flag     TINYINT      NOT NULL DEFAULT 0,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_acct_org_department (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id        BIGINT       NOT NULL,
    tenant_id     BIGINT       NOT NULL,
    dept_name     VARCHAR(128) NOT NULL,
    parent_id     BIGINT       NOT NULL DEFAULT 0,
    sort_no       INT          NOT NULL DEFAULT 0,
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_acct_org_member (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    org_id      BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    dept_id     BIGINT,
    org_role    VARCHAR(32)  NOT NULL DEFAULT 'MEMBER',
    status      VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    joined_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_base_dict_type (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    biz_code    VARCHAR(64)  NOT NULL,
    dict_kind   TINYINT      NOT NULL,
    share_scope VARCHAR(16)  NOT NULL DEFAULT 'global',
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    en_name     VARCHAR(128),
    description VARCHAR(256),
    remark      VARCHAR(256),
    status      TINYINT      NOT NULL DEFAULT 1,
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE (biz_code, dict_kind, share_scope, tenant_id, code)
);

CREATE TABLE IF NOT EXISTS qz_base_dict_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 1,
    biz_code    VARCHAR(64)  NOT NULL,
    type_id     BIGINT       NOT NULL,
    parent_id   BIGINT       NOT NULL DEFAULT 0,
    "value"     VARCHAR(64)  NOT NULL,
    label       VARCHAR(128) NOT NULL,
    en_label    VARCHAR(128),
    remark      VARCHAR(256),
    sort        INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1,
    create_by   BIGINT,
    update_by   BIGINT,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    version     INT          NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, type_id, "value")
);

CREATE TABLE IF NOT EXISTS qz_sys_feature_switch (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id      BIGINT       NOT NULL DEFAULT 1,
    feature_key    VARCHAR(64)  NOT NULL,
    feature_name   VARCHAR(128) NOT NULL,
    segment        VARCHAR(8)   NOT NULL DEFAULT 'ALL',
    enabled        TINYINT      NOT NULL DEFAULT 0,
    gray_rule_json VARCHAR(4096),
    create_by      BIGINT,
    update_by      BIGINT,
    create_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted        TINYINT      NOT NULL DEFAULT 0,
    version        INT          NOT NULL DEFAULT 0,
    UNIQUE (feature_key)
);

CREATE TABLE IF NOT EXISTS qz_sys_security_config (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 1,
    config_key   VARCHAR(64)  NOT NULL,
    config_value CLOB         NOT NULL,
    description  VARCHAR(256),
    create_by    BIGINT,
    update_by    BIGINT,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    version      INT          NOT NULL DEFAULT 0,
    UNIQUE (config_key)
);

CREATE TABLE IF NOT EXISTS qz_sys_audit_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    operator_id   BIGINT,
    operator_name VARCHAR(128),
    action        VARCHAR(64)  NOT NULL,
    message       VARCHAR(512),
    resource_type VARCHAR(64)  NOT NULL,
    resource_id   VARCHAR(64),
    before_json   VARCHAR(4096),
    after_json    VARCHAR(4096),
    ip_address    VARCHAR(64),
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shedlock (
    name       VARCHAR(64)  NOT NULL PRIMARY KEY,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL
);
