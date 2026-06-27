-- PH3: 知识与文件
CREATE TABLE IF NOT EXISTS runtime_knowledge_fragment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code     VARCHAR(64)    NOT NULL COMMENT '租户编码',
    fragment_id     VARCHAR(64)    NOT NULL COMMENT '片段ID',
    knowledge_code  VARCHAR(128)   NOT NULL COMMENT '知识库编码',
    title           VARCHAR(512)   NOT NULL COMMENT '标题',
    content         TEXT           NOT NULL COMMENT '内容',
    tags_json       JSON           NULL COMMENT '标签JSON',
    status          VARCHAR(32)    NOT NULL COMMENT '状态',
    recorded_at     DATETIME(3)    NOT NULL COMMENT '记录时间',
    create_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_knowledge_fragment (tenant_code, fragment_id),
    KEY idx_knowledge_fragment_code (tenant_code, knowledge_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 知识片段';

CREATE TABLE IF NOT EXISTS runtime_scenario_knowledge_binding (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code     VARCHAR(64)    NOT NULL COMMENT '租户编码',
    binding_id      VARCHAR(64)    NOT NULL COMMENT '绑定ID',
    scenario_code   VARCHAR(128)   NOT NULL COMMENT '场景编码',
    knowledge_code  VARCHAR(128)   NOT NULL COMMENT '知识库编码',
    priority        INT            NOT NULL COMMENT '优先级',
    enabled         TINYINT(1)     NOT NULL COMMENT '是否启用',
    recorded_at     DATETIME(3)    NOT NULL COMMENT '记录时间',
    create_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_scenario_knowledge_binding (tenant_code, binding_id),
    KEY idx_scenario_knowledge_binding_scenario (tenant_code, scenario_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 场景知识绑定';

CREATE TABLE IF NOT EXISTS runtime_file_upload_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code     VARCHAR(64)    NOT NULL COMMENT '租户编码',
    file_id         VARCHAR(64)    NOT NULL COMMENT '文件ID',
    file_name       VARCHAR(512)   NOT NULL COMMENT '文件名',
    content_type    VARCHAR(256)   NULL COMMENT '内容类型',
    size_bytes      BIGINT         NOT NULL COMMENT '文件大小字节',
    storage_path    VARCHAR(1024)  NOT NULL COMMENT '存储路径',
    checksum        VARCHAR(128)   NULL COMMENT '校验和',
    status          VARCHAR(32)    NOT NULL COMMENT '上传状态',
    recorded_at     DATETIME(3)    NOT NULL COMMENT '记录时间',
    create_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time     DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_file_upload_record (tenant_code, file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 文件上传记录';

CREATE TABLE IF NOT EXISTS runtime_file_parse_task (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code       VARCHAR(64)    NOT NULL COMMENT '租户编码',
    task_id           VARCHAR(64)    NOT NULL COMMENT '任务ID',
    file_id           VARCHAR(64)    NOT NULL COMMENT '文件ID',
    status            VARCHAR(32)    NOT NULL COMMENT '任务状态',
    parse_result_json TEXT           NULL COMMENT '解析结果JSON',
    error_message     TEXT           NULL COMMENT '错误信息',
    started_at        DATETIME(3)    NULL COMMENT '开始时间',
    finished_at       DATETIME(3)    NULL COMMENT '结束时间',
    recorded_at       DATETIME(3)    NOT NULL COMMENT '记录时间',
    create_time       DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '记录创建时间',
    update_time       DATETIME(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '记录更新时间',
    UNIQUE KEY uk_file_parse_task (tenant_code, task_id),
    KEY idx_file_parse_task_file (tenant_code, file_id, finished_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime 文件解析任务';
