-- 统一表前缀 qz_（研发阶段一次性 RENAME，框架表 oauth2_*/shedlock 保持不变）

-- IAM
RENAME TABLE sys_user TO qz_iam_user;
RENAME TABLE sys_role TO qz_iam_role;
RENAME TABLE sys_permission TO qz_iam_permission;
RENAME TABLE sys_role_permission TO qz_iam_role_permission;
RENAME TABLE sys_user_role TO qz_iam_user_role;
RENAME TABLE sys_tenant TO qz_iam_tenant;

-- 账户组织
RENAME TABLE biz_account TO qz_acct_account;
RENAME TABLE biz_organization TO qz_acct_org;
RENAME TABLE biz_org_department TO qz_acct_org_department;
RENAME TABLE biz_org_member TO qz_acct_org_member;

-- 会员额度
RENAME TABLE biz_membership_level TO qz_mbr_level;
RENAME TABLE biz_account_membership TO qz_mbr_account;
RENAME TABLE biz_membership_change_log TO qz_mbr_membership_change_log;
RENAME TABLE biz_quota_account TO qz_mbr_quota_account;
RENAME TABLE biz_quota_member_alloc TO qz_mbr_quota_member_alloc;
RENAME TABLE biz_token_record TO qz_mbr_token_record;

-- 计费
RENAME TABLE biz_order TO qz_bill_order;
RENAME TABLE biz_subscription TO qz_bill_subscription;
RENAME TABLE biz_subscription_package TO qz_bill_subscription_package;
RENAME TABLE biz_quota_package TO qz_bill_quota_package;
RENAME TABLE biz_financial_record TO qz_bill_financial_record;

-- 知识内容
RENAME TABLE biz_content TO qz_kb_content;
RENAME TABLE biz_content_tag TO qz_kb_content_tag;
RENAME TABLE biz_content_tag_rel TO qz_kb_content_tag_rel;
RENAME TABLE biz_knowledge_package TO qz_kb_knowledge_package;
RENAME TABLE biz_knowledge_package_item TO qz_kb_knowledge_package_item;
RENAME TABLE biz_content_import_job TO qz_kb_content_import_job;

-- 运营
RENAME TABLE biz_banner TO qz_ops_banner;
RENAME TABLE biz_announcement TO qz_ops_announcement;
RENAME TABLE biz_message_template TO qz_ops_message_template;

-- 系统
RENAME TABLE sys_dict_type TO qz_sys_dict_type;
RENAME TABLE sys_dict_item TO qz_sys_dict_item;
RENAME TABLE sys_feature_switch TO qz_sys_feature_switch;
RENAME TABLE sys_security_config TO qz_sys_security_config;
RENAME TABLE sys_audit_log TO qz_sys_audit_log;

-- AI 元数据
RENAME TABLE center_model_definition TO qz_ai_model_definition;
RENAME TABLE center_prompt_template TO qz_ai_prompt_template;
RENAME TABLE center_tool_definition TO qz_ai_tool_definition;
RENAME TABLE center_skill_definition TO qz_ai_skill_definition;
RENAME TABLE center_skill_tool TO qz_ai_skill_tool;
RENAME TABLE center_agent_definition TO qz_ai_agent_definition;
RENAME TABLE center_agent_skill TO qz_ai_agent_skill;
RENAME TABLE center_capability_definition TO qz_ai_capability_definition;
RENAME TABLE center_capability_release_pointer TO qz_ai_capability_release_pointer;
RENAME TABLE center_prompt_release_pointer TO qz_ai_prompt_release_pointer;
RENAME TABLE center_capability_tenant_binding TO qz_ai_capability_tenant_binding;

-- 运行时
RENAME TABLE runtime_execution_record TO qz_rt_execution_record;
RENAME TABLE runtime_usage_record TO qz_rt_usage_record;
RENAME TABLE runtime_usage_account TO qz_rt_usage_account;
RENAME TABLE runtime_trace_span TO qz_rt_trace_span;
RENAME TABLE runtime_audit_log TO qz_rt_audit_log;
RENAME TABLE runtime_conversation_session TO qz_rt_conversation_session;
RENAME TABLE runtime_conversation_message TO qz_rt_conversation_message;
RENAME TABLE runtime_execution_feedback TO qz_rt_execution_feedback;
RENAME TABLE runtime_knowledge_fragment TO qz_rt_knowledge_fragment;
RENAME TABLE runtime_scenario_knowledge_binding TO qz_rt_scenario_knowledge_binding;
RENAME TABLE runtime_file_upload_record TO qz_rt_file_upload_record;
RENAME TABLE runtime_file_parse_task TO qz_rt_file_parse_task;
RENAME TABLE harness_report TO qz_rt_harness_report;
RENAME TABLE harness_step_report TO qz_rt_harness_step_report;
