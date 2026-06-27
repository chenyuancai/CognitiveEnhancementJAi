CREATE TABLE IF NOT EXISTS qz_base_file (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 1,
    biz_code      VARCHAR(64)  NOT NULL DEFAULT 'cog',
    original_name VARCHAR(512) NOT NULL,
    storage_name  VARCHAR(512) NOT NULL,
    storage_path  VARCHAR(1024) NOT NULL,
    content_type  VARCHAR(128),
    size_bytes    BIGINT       NOT NULL DEFAULT 0,
    md5           VARCHAR(64),
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=UNCONFIRMED 2=CONFIRMED',
    create_by     BIGINT,
    update_by     BIGINT,
    create_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    update_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    deleted       TINYINT      NOT NULL DEFAULT 0,
    version       INT          NOT NULL DEFAULT 0,
    KEY idx_base_file_tenant (tenant_id, biz_code, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础文件元数据';
