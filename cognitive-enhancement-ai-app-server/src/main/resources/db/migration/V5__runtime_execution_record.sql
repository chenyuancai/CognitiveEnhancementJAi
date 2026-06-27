-- runtime_execution_record 表：能力执行链路摘要
CREATE TABLE IF NOT EXISTS runtime_execution_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    trace_id        VARCHAR(64)     COMMENT '链路追踪 ID',
    capability_code VARCHAR(64)     COMMENT '能力编码',
    agent_code      VARCHAR(64)     COMMENT 'Agent 编码',
    result_status   VARCHAR(32)     COMMENT '执行结果状态',
    success         TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否成功完成执行',
    failure_reason  VARCHAR(1024)   COMMENT '执行失败原因（成功时为空）',
    recorded_at     DATETIME(3)     NOT NULL COMMENT '记录时间',
    create_time     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    KEY idx_trace_id (trace_id),
    KEY idx_capability_code (capability_code),
    KEY idx_agent_code (agent_code),
    KEY idx_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能力执行链路摘要';
