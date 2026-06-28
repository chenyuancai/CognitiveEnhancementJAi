CREATE TABLE IF NOT EXISTS oauth2_registered_client (
    id                           VARCHAR(100)  NOT NULL,
    client_id                    VARCHAR(100)  NOT NULL,
    client_id_issued_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_secret                VARCHAR(200)  DEFAULT NULL,
    client_secret_expires_at     TIMESTAMP     DEFAULT NULL,
    client_name                  VARCHAR(200)  NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types    VARCHAR(1000) NOT NULL,
    redirect_uris                VARCHAR(1000) DEFAULT NULL,
    post_logout_redirect_uris    VARCHAR(1000) DEFAULT NULL,
    scopes                       VARCHAR(1000) NOT NULL,
    client_settings              VARCHAR(2000) NOT NULL,
    token_settings               VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization (
    id                            VARCHAR(100) NOT NULL,
    registered_client_id          VARCHAR(100) NOT NULL,
    principal_name                VARCHAR(200) NOT NULL,
    authorization_grant_type      VARCHAR(100) NOT NULL,
    authorized_scopes             VARCHAR(1000) DEFAULT NULL,
    attributes                    BLOB         DEFAULT NULL,
    state                         VARCHAR(500) DEFAULT NULL,
    authorization_code_value      BLOB         DEFAULT NULL,
    authorization_code_issued_at  TIMESTAMP    DEFAULT NULL,
    authorization_code_expires_at TIMESTAMP    DEFAULT NULL,
    authorization_code_metadata   BLOB         DEFAULT NULL,
    access_token_value            BLOB         DEFAULT NULL,
    access_token_issued_at        TIMESTAMP    DEFAULT NULL,
    access_token_expires_at       TIMESTAMP    DEFAULT NULL,
    access_token_metadata         BLOB         DEFAULT NULL,
    access_token_type             VARCHAR(100) DEFAULT NULL,
    access_token_scopes           VARCHAR(1000) DEFAULT NULL,
    oidc_id_token_value           BLOB         DEFAULT NULL,
    oidc_id_token_issued_at       TIMESTAMP    DEFAULT NULL,
    oidc_id_token_expires_at      TIMESTAMP    DEFAULT NULL,
    oidc_id_token_metadata        BLOB         DEFAULT NULL,
    refresh_token_value           BLOB         DEFAULT NULL,
    refresh_token_issued_at       TIMESTAMP    DEFAULT NULL,
    refresh_token_expires_at      TIMESTAMP    DEFAULT NULL,
    refresh_token_metadata        BLOB         DEFAULT NULL,
    user_code_value               BLOB         DEFAULT NULL,
    user_code_issued_at           TIMESTAMP    DEFAULT NULL,
    user_code_expires_at          TIMESTAMP    DEFAULT NULL,
    user_code_metadata            BLOB         DEFAULT NULL,
    device_code_value             BLOB         DEFAULT NULL,
    device_code_issued_at         TIMESTAMP    DEFAULT NULL,
    device_code_expires_at        TIMESTAMP    DEFAULT NULL,
    device_code_metadata          BLOB         DEFAULT NULL,
    PRIMARY KEY (id)
);

-- 认证服务用户表（DbUserDetailsService 最小依赖）
CREATE TABLE IF NOT EXISTS qz_iam_user (
    id                 BIGINT       NOT NULL PRIMARY KEY,
    tenant_id          BIGINT       NOT NULL,
    username           VARCHAR(64)  NOT NULL,
    password_hash      VARCHAR(255) NOT NULL,
    nickname           VARCHAR(64)  DEFAULT NULL,
    status             VARCHAR(32)  NOT NULL,
    ban_until          TIMESTAMP    DEFAULT NULL,
    primary_account_id BIGINT       DEFAULT NULL,
    deleted            TINYINT      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS qz_iam_tenant (
    id           BIGINT       NOT NULL PRIMARY KEY,
    tenant_code  VARCHAR(64)  NOT NULL,
    tenant_name  VARCHAR(128) NOT NULL,
    segment      VARCHAR(32)  NOT NULL,
    status       VARCHAR(32)  NOT NULL
);

CREATE TABLE IF NOT EXISTS qz_iam_role (
    id        BIGINT       NOT NULL PRIMARY KEY,
    tenant_id BIGINT       NOT NULL,
    role_code VARCHAR(64)  NOT NULL,
    role_name VARCHAR(64)  NOT NULL,
    status    VARCHAR(32)  NOT NULL
);

CREATE TABLE IF NOT EXISTS qz_iam_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS qz_iam_permission (
    id              BIGINT       NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL,
    permission_code VARCHAR(128) NOT NULL,
    alias_code      VARCHAR(128),
    permission_name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS qz_iam_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

MERGE INTO qz_iam_tenant (id, tenant_code, tenant_name, segment, status) KEY(id) VALUES
    (1, 'default', '启知平台', '2C', 'ENABLED');

MERGE INTO qz_iam_role (id, tenant_id, role_code, role_name, status) KEY(id) VALUES
    (1, 1, 'ADMIN', '超级管理员', 'ENABLED');

MERGE INTO qz_iam_user (id, tenant_id, username, password_hash, nickname, status, primary_account_id) KEY(id) VALUES
    (1, 1, 'admin', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '管理员', 'ENABLED', 1);

MERGE INTO qz_iam_user_role (user_id, role_id) KEY(user_id, role_id) VALUES (1, 1);
