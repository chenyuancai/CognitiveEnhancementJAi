-- Center 元数据定义表
-- 包含模型、提示词模板、工具、技能、Agent、能力 6 种实体及其关联关系

-- ==================== 模型定义 ====================
CREATE TABLE IF NOT EXISTS center_model_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    provider_code VARCHAR(64) NOT NULL COMMENT '提供商编码',
    provider_name VARCHAR(128) NOT NULL COMMENT '提供商名称',
    model_code VARCHAR(64) NOT NULL UNIQUE COMMENT '模型编码',
    model_name VARCHAR(128) NOT NULL COMMENT '模型名称',
    model_type VARCHAR(32) NOT NULL COMMENT '模型类型',
    endpoint VARCHAR(512) NOT NULL COMMENT '调用地址',
    credential_ref VARCHAR(128) COMMENT '凭证引用',
    timeout_ms INT NOT NULL COMMENT '超时时间(ms)',
    retry_times INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    status VARCHAR(16) NOT NULL COMMENT '状态：ENABLED/DISABLED',
    route_priority INT NOT NULL DEFAULT 0 COMMENT '路由优先级',
    fallback_model_code VARCHAR(64) COMMENT '降级模型编码',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模型定义';

-- ==================== 提示词模板 ====================
CREATE TABLE IF NOT EXISTS center_prompt_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    prompt_code VARCHAR(64) NOT NULL UNIQUE COMMENT '提示词编码',
    prompt_name VARCHAR(128) NOT NULL COMMENT '提示词名称',
    scenario_code VARCHAR(64) NOT NULL COMMENT '场景编码',
    version VARCHAR(32) NOT NULL COMMENT '版本',
    template_content TEXT NOT NULL COMMENT '模板内容',
    variable_schema JSON COMMENT '变量Schema',
    output_schema JSON COMMENT '输出Schema',
    status VARCHAR(16) NOT NULL COMMENT '状态：ENABLED/DISABLED',
    published_at DATETIME COMMENT '发布时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_scenario (scenario_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词模板';

-- ==================== 工具定义 ====================
CREATE TABLE IF NOT EXISTS center_tool_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    tool_code VARCHAR(64) NOT NULL UNIQUE COMMENT '工具编码',
    tool_name VARCHAR(128) NOT NULL COMMENT '工具名称',
    protocol_type VARCHAR(32) NOT NULL COMMENT '协议类型',
    request_schema JSON COMMENT '请求Schema',
    response_schema JSON COMMENT '响应Schema',
    permission_scope VARCHAR(128) NOT NULL COMMENT '权限范围',
    timeout_ms INT NOT NULL COMMENT '超时时间(ms)',
    retry_max_attempts INT NOT NULL DEFAULT 1 COMMENT '最大重试次数',
    impl_ref VARCHAR(128) NOT NULL COMMENT '实现引用',
    status VARCHAR(16) NOT NULL COMMENT '状态：ENABLED/DISABLED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具定义';

-- ==================== 技能定义 ====================
CREATE TABLE IF NOT EXISTS center_skill_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    skill_code VARCHAR(64) NOT NULL UNIQUE COMMENT '技能编码',
    skill_name VARCHAR(128) NOT NULL COMMENT '技能名称',
    skill_type VARCHAR(32) NOT NULL COMMENT '技能类型',
    skill_instruction TEXT NOT NULL COMMENT '技能指令',
    risk_level VARCHAR(16) NOT NULL COMMENT '风险等级：LOW/MEDIUM/HIGH/CRITICAL',
    forbidden_rules JSON COMMENT '禁止规则列表',
    examples JSON COMMENT '示例列表',
    status VARCHAR(16) NOT NULL COMMENT '状态：ENABLED/DISABLED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能定义';

CREATE TABLE IF NOT EXISTS center_skill_tool (
    skill_code VARCHAR(64) NOT NULL COMMENT '技能编码',
    tool_code VARCHAR(64) NOT NULL COMMENT '工具编码',
    PRIMARY KEY (skill_code, tool_code),
    INDEX idx_tool (tool_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能-工具关联';

-- ==================== Agent 定义 ====================
CREATE TABLE IF NOT EXISTS center_agent_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    agent_code VARCHAR(64) NOT NULL UNIQUE COMMENT 'Agent编码',
    agent_name VARCHAR(128) NOT NULL COMMENT 'Agent名称',
    role_desc VARCHAR(256) NOT NULL COMMENT '角色描述',
    goal_desc VARCHAR(256) NOT NULL COMMENT '目标描述',
    model_code VARCHAR(64) NOT NULL COMMENT '模型编码',
    max_steps INT NOT NULL COMMENT '最大步骤数',
    max_cost DECIMAL(19,4) NOT NULL COMMENT '最大成本',
    timeout_ms INT NOT NULL COMMENT '超时时间(ms)',
    parameter_constraints JSON COMMENT '参数约束',
    status VARCHAR(16) NOT NULL COMMENT '状态：ENABLED/DISABLED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_model (model_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent定义';

CREATE TABLE IF NOT EXISTS center_agent_skill (
    agent_code VARCHAR(64) NOT NULL COMMENT 'Agent编码',
    skill_code VARCHAR(64) NOT NULL COMMENT '技能编码',
    PRIMARY KEY (agent_code, skill_code),
    INDEX idx_skill (skill_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent-技能关联';

-- ==================== 能力定义 ====================
CREATE TABLE IF NOT EXISTS center_capability_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    capability_code VARCHAR(64) NOT NULL UNIQUE COMMENT '能力编码',
    capability_name VARCHAR(128) NOT NULL COMMENT '能力名称',
    capability_desc VARCHAR(512) NOT NULL COMMENT '能力描述',
    input_schema JSON COMMENT '输入Schema',
    output_schema JSON COMMENT '输出Schema',
    parameter_constraints JSON COMMENT '参数约束',
    execute_mode VARCHAR(16) NOT NULL COMMENT '执行模式：SYNC/ASYNC',
    bound_agent_code VARCHAR(64) NOT NULL COMMENT '绑定Agent编码',
    risk_level VARCHAR(16) NOT NULL COMMENT '风险等级：LOW/MEDIUM/HIGH/CRITICAL',
    need_human_confirm TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否需要人工确认：0-否 1-是',
    status VARCHAR(16) NOT NULL COMMENT '状态：ENABLED/DISABLED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_agent (bound_agent_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='能力定义';

-- ==================== 演示数据 ====================

-- 模型演示数据
INSERT IGNORE INTO center_model_definition
    (provider_code, provider_name, model_code, model_name, model_type, endpoint, credential_ref, timeout_ms, retry_times, status, route_priority, fallback_model_code)
VALUES
    ('openai', 'OpenAI', 'gpt-4o-mini', 'GPT-4o Mini', 'CHAT', 'https://api.openai.com/v1/chat/completions', 'credential/openai/default', 30000, 2, 'ENABLED', 10, NULL),
    ('bailian', '阿里云百炼', 'qwen-plus', 'Qwen Plus', 'CHAT', 'https://dashscope.aliyuncs.com/compatible-mode/v1', '__DASHSCOPE_API_KEY__', 30000, 2, 'ENABLED', 20, 'gpt-4o-mini');

-- 提示词模板演示数据
INSERT IGNORE INTO center_prompt_template
    (prompt_code, prompt_name, scenario_code, version, template_content, variable_schema, output_schema, status, published_at)
VALUES
    ('prompt.qa.default', '默认问答模板', 'qa', 'v1',
     '请结合上下文与工具结果回答用户问题：{{question}}',
     '{"type":"object","description":"能力输入","required":true,"properties":{"question":{"type":"string","description":"用户问题","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"type":"object","description":"能力输出","required":true,"properties":{"answer":{"type":"string","description":"回答内容","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     'ENABLED', '2026-05-11 00:00:00'),
    ('prompt.chat.default', '默认对话模板', 'chat', 'v1',
     '请以助手身份直接回答用户问题：{{question}}',
     '{"type":"object","description":"能力输入","required":true,"properties":{"question":{"type":"string","description":"用户问题","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"type":"object","description":"能力输出","required":true,"properties":{"answer":{"type":"string","description":"回答内容","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     'ENABLED', '2026-05-11 00:00:00');

-- 工具演示数据
INSERT IGNORE INTO center_tool_definition
    (tool_code, tool_name, protocol_type, request_schema, response_schema, permission_scope, timeout_ms, retry_max_attempts, impl_ref, status)
VALUES
    ('tool.search', '搜索工具', 'JAVA_LOCAL',
     '{"type":"object","description":"能力输入","required":true,"properties":{"question":{"type":"string","description":"用户问题","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"type":"object","description":"能力输出","required":true,"properties":{"answer":{"type":"string","description":"回答内容","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     'search:query', 5000, 1, 'demoSearchTool', 'ENABLED');

-- 技能演示数据
INSERT IGNORE INTO center_skill_definition
    (skill_code, skill_name, skill_type, skill_instruction, risk_level, forbidden_rules, examples, status)
VALUES
    ('skill.qa', '问答技能', 'DOMAIN', '优先基于事实回答，不确定时明确说明。', 'LOW',
     '["不得编造来源"]', '["用户询问事实类问题时可先搜索"]', 'ENABLED'),
    ('skill.chat', '对话技能', 'GENERAL', '直接结合提示词完成回答。', 'LOW',
     '["不得输出攻击性内容"]', '["适合纯对话与常规生成场景"]', 'ENABLED');

INSERT IGNORE INTO center_skill_tool (skill_code, tool_code) VALUES
    ('skill.qa', 'tool.search');

-- Agent 演示数据
INSERT IGNORE INTO center_agent_definition
    (agent_code, agent_name, role_desc, goal_desc, model_code, max_steps, max_cost, timeout_ms, parameter_constraints, status)
VALUES
    ('agent.qa', '问答代理', '专业问答助手', '为用户输出可靠答案', 'gpt-4o-mini', 6, 1.5000, 20000, '{}', 'ENABLED'),
    ('agent.chat', '对话代理', '通用聊天助手', '为用户生成自然语言回答', 'gpt-4o-mini', 4, 1.0000, 20000,
     '{"temperature":{"parameterType":"number","required":false,"minimum":0.0,"maximum":1.5,"integerOnly":false},"topP":{"parameterType":"number","required":false,"minimum":0.1,"maximum":1.0,"integerOnly":false},"maxTokens":{"parameterType":"integer","required":false,"minimum":1.0,"maximum":4096.0,"integerOnly":true}}',
     'ENABLED'),
    ('agent.chat.bailian', '百炼对话代理', '基于阿里云百炼的通用聊天助手', '为用户生成自然语言回答', 'qwen-plus', 4, 1.0000, 20000,
     '{"temperature":{"parameterType":"number","required":false,"minimum":0.0,"maximum":1.5,"integerOnly":false},"topP":{"parameterType":"number","required":false,"minimum":0.1,"maximum":1.0,"integerOnly":false},"maxTokens":{"parameterType":"integer","required":false,"minimum":1.0,"maximum":4096.0,"integerOnly":true}}',
     'ENABLED');

INSERT IGNORE INTO center_agent_skill (agent_code, skill_code) VALUES
    ('agent.qa', 'skill.qa'),
    ('agent.chat', 'skill.chat'),
    ('agent.chat.bailian', 'skill.chat');

-- 能力演示数据
INSERT IGNORE INTO center_capability_definition
    (capability_code, capability_name, capability_desc, input_schema, output_schema, parameter_constraints, execute_mode, bound_agent_code, risk_level, need_human_confirm, status)
VALUES
    ('capability.qa.answer', '智能问答', '对外提供基础问答能力',
     '{"type":"object","description":"能力输入","required":true,"properties":{"question":{"type":"string","description":"用户问题","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"type":"object","description":"能力输出","required":true,"properties":{"answer":{"type":"string","description":"回答内容","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"temperature":{"parameterType":"number","required":false,"minimum":0.0,"maximum":1.0,"integerOnly":false},"topP":{"parameterType":"number","required":false,"minimum":0.1,"maximum":1.0,"integerOnly":false}}',
     'SYNC', 'agent.qa', 'LOW', 0, 'ENABLED'),
    ('capability.chat.generate', '智能对话', '对外提供基础对话生成能力',
     '{"type":"object","description":"能力输入","required":true,"properties":{"question":{"type":"string","description":"用户问题","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"type":"object","description":"能力输出","required":true,"properties":{"answer":{"type":"string","description":"回答内容","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"temperature":{"parameterType":"number","required":false,"minimum":0.0,"maximum":2.0,"integerOnly":false},"topP":{"parameterType":"number","required":false,"minimum":0.01,"maximum":1.0,"integerOnly":false},"maxTokens":{"parameterType":"integer","required":false,"minimum":1.0,"maximum":8192.0,"integerOnly":true}}',
     'SYNC', 'agent.chat', 'LOW', 0, 'ENABLED'),
    ('capability.chat.generate.bailian', '百炼智能对话', '对外提供基于百炼模型的对话生成能力',
     '{"type":"object","description":"能力输入","required":true,"properties":{"question":{"type":"string","description":"用户问题","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"type":"object","description":"能力输出","required":true,"properties":{"answer":{"type":"string","description":"回答内容","required":true,"properties":{},"items":null,"enumValues":[]}},"items":null,"enumValues":[]}',
     '{"temperature":{"parameterType":"number","required":false,"minimum":0.0,"maximum":2.0,"integerOnly":false},"topP":{"parameterType":"number","required":false,"minimum":0.01,"maximum":1.0,"integerOnly":false},"maxTokens":{"parameterType":"integer","required":false,"minimum":1.0,"maximum":8192.0,"integerOnly":true}}',
     'SYNC', 'agent.chat.bailian', 'LOW', 0, 'ENABLED');
