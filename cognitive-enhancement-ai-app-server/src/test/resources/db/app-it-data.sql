DELETE FROM qz_kb_knowledge_package_item;
DELETE FROM qz_kb_knowledge_package;
DELETE FROM qz_kb_content_tag_rel;
DELETE FROM qz_kb_content_tag;
DELETE FROM qz_kb_content_version;
DELETE FROM qz_kb_content;
DELETE FROM qz_ops_in_app_message;
DELETE FROM qz_ops_support_ticket;
DELETE FROM qz_ops_banner;
DELETE FROM qz_ops_announcement;
DELETE FROM qz_sys_audit_log;
DELETE FROM qz_sys_security_config;
DELETE FROM qz_iam_role_permission;
DELETE FROM qz_iam_user_role;
DELETE FROM qz_mbr_token_record;
DELETE FROM qz_bill_financial_record;
DELETE FROM qz_bill_subscription;
DELETE FROM qz_bill_order;
DELETE FROM qz_bill_quota_package;
DELETE FROM qz_bill_subscription_package;
DELETE FROM qz_mbr_quota_account;
DELETE FROM qz_mbr_account;
DELETE FROM qz_mbr_membership_change_log;
DELETE FROM qz_mbr_level_benefit;
DELETE FROM qz_mbr_benefit_def;
DELETE FROM qz_mbr_level;
DELETE FROM qz_acct_org;
DELETE FROM qz_acct_account;
DELETE FROM qz_iam_permission;
DELETE FROM qz_iam_role;
DELETE FROM qz_iam_user;
DELETE FROM qz_iam_tenant;

INSERT INTO qz_iam_tenant (id, tenant_code, tenant_name, segment, status) VALUES
    (1, 'platform', '启知平台', '2C', 'ENABLED');

INSERT INTO qz_iam_role (id, tenant_id, role_code, role_name, status, builtin) VALUES
    (1, 1, 'ADMIN', '超级管理员', 'ENABLED', 1);

INSERT INTO qz_iam_user (id, tenant_id, username, password_hash, nickname, status, user_type, primary_account_id) VALUES
    (1, 1, 'admin', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '管理员', 'ENABLED', 'ADMIN', 1),
    (2, 1, 'pro-user', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '专业用户', 'ENABLED', 'CUSTOMER', 2);

INSERT INTO qz_iam_user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO qz_acct_account (id, tenant_id, account_type, segment, display_name, owner_user_id, status) VALUES
    (1, 1, 'INDIVIDUAL', '2C', '管理员', 1, 'ENABLED'),
    (2, 1, 'INDIVIDUAL', '2C', '专业用户', 2, 'ENABLED');

INSERT INTO qz_mbr_level (id, tenant_id, level_code, level_name, segment, is_default, sort_no, benefits_json) VALUES
    (1, 1, 'FREE', '免费版', '2C', 1, 10, '{"ai.scoring":false,"ai.tutoring":false,"ai.qa_global":true}'),
    (2, 1, 'PRO', '专业版', '2C', 0, 20, '{"ai.scoring":true,"ai.tutoring":true,"ai.qa_global":true}');

INSERT INTO qz_mbr_benefit_def (id, tenant_id, benefit_code, benefit_name, category, value_type) VALUES
    (1, 1, 'ai.scoring', 'AI 评分', 'FUNCTION', 'BOOL'),
    (2, 1, 'ai.tutoring', 'AI 带学', 'FUNCTION', 'BOOL'),
    (3, 1, 'ai.qa_global', '全局 AI 问答', 'FUNCTION', 'BOOL');

INSERT INTO qz_mbr_level_benefit (tenant_id, level_id, benefit_code, benefit_value) VALUES
    (1, 1, 'ai.qa_global', 'true'),
    (1, 2, 'ai.scoring', 'true'),
    (1, 2, 'ai.tutoring', 'true'),
    (1, 2, 'ai.qa_global', 'true');

INSERT INTO qz_mbr_account (tenant_id, account_id, level_id, level_code, source) VALUES
    (1, 1, 1, 'FREE', 'DEFAULT'),
    (1, 2, 2, 'PRO', 'PURCHASE');

INSERT INTO qz_mbr_quota_account (tenant_id, account_id, cycle_remaining, cycle_total, gift_remaining, topup_remaining) VALUES
    (1, 1, 100000, 100000, 0, 0),
    (1, 2, 200000, 200000, 0, 0);

INSERT INTO qz_iam_permission (id, permission_code, alias_code, permission_name, kind, scope, module_key, sort_no, builtin, status) VALUES
    (1, 'iam:user:read', 'admin:user:view', '查看用户', 'action', 'admin', 'account-governance', 10, 1, 'ENABLED'),
    (2, 'billing:order:update', 'admin:order:update', '手动标记订单', 'action', 'admin', 'billing', 50, 1, 'ENABLED'),
    (3, 'billing:order:refund', 'admin:order:refund', '发起退款', 'action', 'admin', 'billing', 51, 1, 'ENABLED'),
    (4, 'workbench:view', 'admin:workbench:view', '查看工作台', 'action', 'admin', 'workbench', 5, 1, 'ENABLED');

INSERT INTO qz_iam_role_permission (role_id, permission_id) VALUES (1, 1), (1, 2), (1, 3), (1, 4);

INSERT INTO qz_kb_content (id, tenant_id, title, content_type, author, status, summary, body, min_level_code, current_version, published_at) VALUES
    (1, 1, 'Phase3 测试文章', 'ARTICLE', '系统', 'PUBLISHED', '面向新用户的平台入门说明', '正文内容', 'FREE', 1, CURRENT_TIMESTAMP);

INSERT INTO qz_kb_content_tag (id, tenant_id, tag_name, tag_color) VALUES
    (1, 1, '入门', '#409EFF');

INSERT INTO qz_kb_content_tag_rel (content_id, tag_id) VALUES (1, 1);

INSERT INTO qz_kb_knowledge_package (id, tenant_id, package_name, description, status) VALUES
    (1, 1, '默认知识包', '系统内置演示知识包', 'ENABLED');

INSERT INTO qz_kb_knowledge_package_item (id, package_id, parent_id, content_id, title, sort_no) VALUES
    (1, 1, 0, 1, 'Phase3 测试文章', 1);

INSERT INTO qz_ops_banner (id, tenant_id, title, image_url, link_url, position, sort_no, status) VALUES
    (1, 1, '首页 Banner', 'https://example.com/banner.png', 'https://example.com', 'HOME_TOP', 1, 'ENABLED');

INSERT INTO qz_ops_announcement (id, tenant_id, title, body, status, publish_at, target_level_codes, target_user_ids) VALUES
    (1, 1, '系统公告', '欢迎使用启知', 'PUBLISHED', CURRENT_TIMESTAMP, NULL, NULL),
    (2, 1, 'PRO 专属公告', '专业版用户可见', 'PUBLISHED', CURRENT_TIMESTAMP, 'PRO', NULL),
    (3, 1, '定向用户公告', '仅用户 2 可见', 'PUBLISHED', CURRENT_TIMESTAMP, NULL, '2');

INSERT INTO qz_ops_in_app_message (id, tenant_id, user_id, template_code, title, content, read_flag) VALUES
    (1, 1, 1, 'welcome', 'welcome', '欢迎使用启知', 0);

INSERT INTO qz_bill_quota_package (id, tenant_id, package_code, package_name, segment, token_amount, price_fen, valid_days, status) VALUES
    (1, 1, 'quota.starter', '入门加油包', '2C', 50000, 9900, 365, 'ON_SALE');

INSERT INTO qz_bill_subscription_package (id, tenant_id, package_code, package_name, segment, level_id, billing_period, trial_days, price_fen, cycle_token_quota, status) VALUES
    (1, 1, 'sub.pro.monthly', '专业版月付', '2C', 2, 'MONTH', 7, 19900, 200000, 'ON_SALE');

INSERT INTO qz_sys_security_config (id, tenant_id, config_key, config_value, description) VALUES
    (1, 1, 'auth.register.username', 'true', '用户名密码注册'),
    (2, 1, 'auth.register.phone', 'false', '手机号注册'),
    (3, 1, 'auth.register.email', 'false', '邮箱注册'),
    (4, 1, 'order.pendingTimeoutMinutes', '30', '待支付超时分钟'),
    (5, 1, 'refund.revokeMembership', 'true', '退款回收会员'),
    (6, 1, 'refund.clawbackUnused', 'true', '退款扣回未用加油额度');
