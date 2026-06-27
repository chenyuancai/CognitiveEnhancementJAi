-- 执行记录链路详情字段：输入 / 路由 / 结果 JSON
ALTER TABLE runtime_execution_record
    ADD COLUMN input_json   JSON NULL COMMENT '执行输入参数 JSON' AFTER failure_reason,
    ADD COLUMN routing_json JSON NULL COMMENT '路由装配摘要 JSON' AFTER input_json,
    ADD COLUMN result_json  JSON NULL COMMENT '执行结果摘要 JSON' AFTER routing_json;
