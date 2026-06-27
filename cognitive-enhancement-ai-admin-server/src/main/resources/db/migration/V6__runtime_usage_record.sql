-- runtime_usage_record 表：能力调用用量记录
CREATE TABLE IF NOT EXISTS runtime_usage_record (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    trace_id            VARCHAR(64)     COMMENT '链路追踪 ID',
    capability_code     VARCHAR(64)     COMMENT '能力编码',
    agent_code          VARCHAR(64)     COMMENT 'Agent 编码',
    executor_type       VARCHAR(32)     COMMENT '执行器类型',
    model_code          VARCHAR(64)     COMMENT '模型编码',
    tool_code           VARCHAR(64)     COMMENT 'Tool 编码',
    input_token_count   INT             NOT NULL DEFAULT 0 COMMENT '输入 token 数',
    output_token_count  INT             NOT NULL DEFAULT 0 COMMENT '输出 token 数',
    total_token_count   INT             NOT NULL DEFAULT 0 COMMENT '总 token 数',
    estimated_cost_amount DECIMAL(18,6) COMMENT '预估成本',
    recorded_at         DATETIME(3)     NOT NULL COMMENT '记录时间',
    create_time         DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time         DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    KEY idx_trace_id (trace_id),
    KEY idx_capability_code (capability_code),
    KEY idx_model_code (model_code),
    KEY idx_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能力调用用量记录';
