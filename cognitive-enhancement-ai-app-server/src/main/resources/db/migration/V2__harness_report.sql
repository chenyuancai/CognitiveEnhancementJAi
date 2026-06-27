-- harness_report 表：报告主数据
CREATE TABLE IF NOT EXISTS harness_report (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    harness_id      VARCHAR(32)     NOT NULL COMMENT 'Harness 执行标识',
    trace_id        VARCHAR(64)     COMMENT '链路追踪 ID',
    status          VARCHAR(16)     NOT NULL COMMENT '整体状态：RUNNING/PASSED/PARTIAL/FAILED',
    start_time      DATETIME(3)     NOT NULL COMMENT '开始时间',
    end_time        DATETIME(3)     COMMENT '结束时间',
    total_duration_ms BIGINT        COMMENT '总耗时（毫秒）',
    scenario_json   JSON            COMMENT '场景摘要（JSON）',
    summary_json    JSON            COMMENT '执行摘要（JSON）',
    create_time     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    UNIQUE KEY uk_harness_id (harness_id),
    KEY idx_start_time (start_time),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Harness 执行报告';

-- harness_step_report 表：步骤详情
CREATE TABLE IF NOT EXISTS harness_step_report (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    harness_id      VARCHAR(32)     NOT NULL COMMENT '关联 Harness 标识',
    sequence        INT             NOT NULL COMMENT '步骤序号',
    step_code       VARCHAR(32)     NOT NULL COMMENT '步骤编码',
    step_name       VARCHAR(64)     NOT NULL COMMENT '步骤名称',
    status          VARCHAR(16)     NOT NULL COMMENT '状态：PASSED/FAILED/SKIPPED',
    duration_ms     BIGINT          COMMENT '执行耗时（毫秒）',
    message         VARCHAR(512)    COMMENT '结果说明',
    details_json    JSON            COMMENT '扩展信息（JSON）',
    create_time     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time     DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    KEY idx_harness_id (harness_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Harness 步骤报告';
