-- 导入工作流扩展：任务字段 + 知识分块/向量表
ALTER TABLE qz_app_import_task
    ADD COLUMN import_biz_type VARCHAR(64) NULL AFTER channel,
    ADD COLUMN file_id BIGINT NULL AFTER file_name,
    ADD COLUMN file_url VARCHAR(512) NULL AFTER file_id,
    ADD COLUMN result_json TEXT NULL AFTER library_item_id;

CREATE TABLE IF NOT EXISTS qz_kb_content_chunk (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL,
    content_id   BIGINT       NOT NULL,
    task_code    VARCHAR(64)  NULL,
    chunk_index  INT          NOT NULL,
    heading_path VARCHAR(512) NULL,
    chunk_text   MEDIUMTEXT   NOT NULL,
    token_est    INT          NULL,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_kb_chunk_content (tenant_id, content_id, chunk_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS qz_kb_vector_index_record (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL,
    content_id   BIGINT       NOT NULL,
    chunk_id     BIGINT       NOT NULL,
    model_code   VARCHAR(128) NULL,
    dim          INT          NOT NULL,
    vector_json  MEDIUMTEXT   NOT NULL,
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_kb_vector_chunk (tenant_id, chunk_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
