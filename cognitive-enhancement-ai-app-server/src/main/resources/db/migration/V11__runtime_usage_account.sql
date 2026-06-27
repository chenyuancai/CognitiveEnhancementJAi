-- runtime_usage_account 表：租户用量额度账户
CREATE TABLE IF NOT EXISTS runtime_usage_account (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code     VARCHAR(64)    NOT NULL COMMENT '租户编码',
    balance_amount  DECIMAL(18,6)  NOT NULL DEFAULT 0.000000 COMMENT '剩余额度',
    used_amount     DECIMAL(18,6)  NOT NULL DEFAULT 0.000000 COMMENT '已用额度',
    enabled         TINYINT(1)     NOT NULL DEFAULT 1 COMMENT '是否启用额度拦截',
    updated_at      DATETIME(3)    NOT NULL COMMENT '账户更新时间',
    create_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    UNIQUE KEY uk_usage_account_tenant_code (tenant_code),
    KEY idx_usage_account_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户用量额度账户';
