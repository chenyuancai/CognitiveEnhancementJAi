-- runtime_audit_log 表：配置变更与运行调用审计日志
CREATE TABLE IF NOT EXISTS runtime_audit_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    tenant_code   VARCHAR(64)   NOT NULL DEFAULT 'default' COMMENT '租户编码',
    trace_id      VARCHAR(64)   COMMENT '链路追踪 ID',
    event_type    VARCHAR(64)   NOT NULL COMMENT '事件类型',
    action        VARCHAR(64)   NOT NULL COMMENT '操作动作',
    resource_type VARCHAR(128)  COMMENT '资源类型',
    resource_code VARCHAR(128)  COMMENT '资源编码',
    operator      VARCHAR(128)  COMMENT '操作人',
    success       TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '是否成功',
    detail_json   JSON          NULL COMMENT '审计详情 JSON',
    recorded_at   DATETIME(3)   NOT NULL COMMENT '记录时间',
    create_time   DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time   DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    KEY idx_audit_tenant_code (tenant_code),
    KEY idx_audit_trace_id (trace_id),
    KEY idx_audit_event_type (event_type),
    KEY idx_audit_resource (resource_type, resource_code),
    KEY idx_audit_recorded_at (recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志';
