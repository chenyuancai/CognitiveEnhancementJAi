-- 统一调用流水 TraceSpan

CREATE TABLE IF NOT EXISTS runtime_trace_span (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tenant_code     VARCHAR(64)  NOT NULL DEFAULT 'default' COMMENT '租户编码',
    trace_id        VARCHAR(64)  NOT NULL COMMENT '链路 ID',
    span_id         VARCHAR(64)  NOT NULL COMMENT '步骤 ID',
    parent_span_id  VARCHAR(64)  NULL COMMENT '父步骤 ID',
    span_type       VARCHAR(32)  NOT NULL COMMENT 'CAPABILITY/AGENT/TOOL/LLM/POLICY/QUOTA',
    span_name       VARCHAR(128) NOT NULL COMMENT '步骤名称',
    status          VARCHAR(16)  NOT NULL COMMENT 'SUCCESS/FAILED/SKIPPED',
    latency_ms      BIGINT       NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
    attributes_json JSON         NULL COMMENT '扩展属性',
    error_stack     TEXT         NULL COMMENT '失败栈摘要',
    recorded_at     DATETIME(3)  NOT NULL COMMENT '记录时间',
    create_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    KEY idx_trace_span_tenant_trace (tenant_code, trace_id),
    KEY idx_trace_span_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Runtime Trace Span 调用流水';
