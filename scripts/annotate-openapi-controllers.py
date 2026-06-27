#!/usr/bin/env python3
"""为 REST 控制器批量添加 OpenAPI @Tag / @Operation 注解。"""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

IMPORTS = (
    "import io.swagger.v3.oas.annotations.Operation;\n"
    "import io.swagger.v3.oas.annotations.tags.Tag;\n"
)

PUBLIC_IMPORT = "import io.swagger.v3.oas.annotations.security.SecurityRequirements;\n"

CONTROLLERS: list[dict] = [
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/user/AuthController.java",
        "tag": "鉴权",
        "tag_desc": "用户登录与注册，获取 JWT Token",
        "public": True,
        "ops": {
            "login": ("用户登录", "使用用户名和密码登录，返回 JWT Token 与用户信息。前端请将 Token 写入 Authorization 请求头。"),
            "register": ("用户注册", "注册新用户并自动返回 JWT Token。无需预先登录。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/user/UserController.java",
        "tag": "用户管理",
        "tag_desc": "用户注册、登录与用户列表查询（含 ADMIN 权限校验）",
        "public_methods": {"register", "login"},
        "ops": {
            "register": ("用户注册（Users API）", "兼容路径 POST /api/users/register，注册后返回 Token。"),
            "login": ("用户登录（Users API）", "兼容路径 POST /api/users/login，登录后返回 Token。"),
            "getById": ("按 ID 查询用户", "需要 ADMIN 角色。根据用户主键查询用户详情。"),
            "listUsers": ("分页查询用户列表", "需要 ADMIN 角色。支持 page、size 分页参数。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/agent/AgentAdminController.java",
        "tag": "Center - Agent",
        "tag_desc": "Agent 元数据管理：定义角色、目标、绑定模型与技能",
        "ops": {
            "listAll": ("分页查询 Agent 列表", "支持 status、modelCode 等筛选与 page/size/sort 分页排序。"),
            "getByCode": ("查询 Agent 详情", "按 agentCode 返回完整 Agent 定义。"),
            "create": ("创建 Agent", "新增 Agent 定义，需指定 modelCode、allowedSkillCodes、maxCost 等字段。"),
            "update": ("更新 Agent", "按 agentCode 更新 Agent 定义。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/capability/CapabilityAdminController.java",
        "tag": "Center - Capability",
        "tag_desc": "能力元数据与发布治理：版本、灰度、租户启停",
        "ops": {
            "listAll": ("分页查询能力列表", "支持 boundAgentCode、riskLevel、status 等筛选。"),
            "getByCode": ("查询能力详情", "按 capabilityCode 返回能力定义。"),
            "create": ("创建能力", "新增能力定义并绑定 Agent、输入输出 Schema。"),
            "update": ("更新能力", "按 capabilityCode 更新能力定义。"),
            "listVersions": ("查询能力版本列表", "返回同一 capabilityCode 下的全部版本。"),
            "createDraft": ("创建能力草稿版本", "基于已发布版本创建新的草稿版本。"),
            "publish": ("发布能力版本", "将指定版本设为已发布，供 Runtime 解析。"),
            "configureGray": ("配置能力灰度", "设置灰度版本与流量比例。"),
            "configureTenant": ("配置租户能力启停", "按租户启用或停用指定能力。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/model/ModelAdminController.java",
        "tag": "Center - Model",
        "tag_desc": "模型元数据管理：Provider、Endpoint、降级与路由优先级",
        "ops": {
            "listAll": ("分页查询模型列表", "支持 providerCode、modelType、status 筛选。"),
            "getByCode": ("查询模型详情", "按 modelCode 返回模型定义。"),
            "create": ("创建模型", "新增模型定义，含 endpoint、credentialRef、fallbackModelCode。"),
            "update": ("更新模型", "按 modelCode 更新模型定义。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/prompt/PromptAdminController.java",
        "tag": "Center - Prompt",
        "tag_desc": "Prompt 模板管理与发布：版本、灰度、下线",
        "ops": {
            "listAll": ("分页查询 Prompt 列表", "支持 scenarioCode、status 等筛选。"),
            "getByCode": ("查询 Prompt 详情", "按 promptCode 返回模板内容与 Schema。"),
            "create": ("创建 Prompt", "新增 Prompt 模板。"),
            "update": ("更新 Prompt", "按 promptCode 更新模板。"),
            "listVersions": ("查询 Prompt 版本列表", "返回同一 promptCode 下的全部版本。"),
            "createDraft": ("创建 Prompt 草稿", "基于现有模板创建草稿新版本。"),
            "publish": ("发布 Prompt 版本", "将指定版本设为已发布。"),
            "offline": ("下线 Prompt 版本", "将指定版本标记为下线。"),
            "configureGray": ("配置 Prompt 灰度", "设置灰度版本与流量规则。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/skill/SkillAdminController.java",
        "tag": "Center - Skill",
        "tag_desc": "Skill 技能元数据管理：绑定 Tool、依赖与输出治理规则",
        "ops": {
            "listAll": ("分页查询 Skill 列表", "支持 skillType、status 筛选。"),
            "getByCode": ("查询 Skill 详情", "按 skillCode 返回技能定义。"),
            "create": ("创建 Skill", "新增技能并配置 boundToolCodes。"),
            "update": ("更新 Skill", "按 skillCode 更新技能定义。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-center/src/main/java/cn/cyc/ai/cog/center/tool/ToolAdminController.java",
        "tag": "Center - Tool",
        "tag_desc": "Tool 工具元数据管理：JAVA_LOCAL / HTTP / MCP 协议配置",
        "ops": {
            "listAll": ("分页查询 Tool 列表", "支持 protocolType、status 筛选。"),
            "getByCode": ("查询 Tool 详情", "按 toolCode 返回 Tool 定义与 implRef。"),
            "create": ("创建 Tool", "新增 Tool 定义。"),
            "update": ("更新 Tool", "按 toolCode 更新 Tool 定义。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/web/CapabilityRuntimeController.java",
        "tag": "Runtime - 能力执行",
        "tag_desc": "能力运行时入口：同步执行与 SSE 流式执行",
        "ops": {
            "execute": ("同步执行能力", "走 Capability→Agent→Tool/LLM 主链路。parameters 可透传 PH5 治理开关（planningEnabled、reflectionEnabled 等）。"),
            "executeStream": ("流式执行能力（SSE）", "以 text/event-stream 推送 STARTED/COMPLETED/FAILED 事件，业务语义与同步接口一致。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/web/HarnessController.java",
        "tag": "Admin - Harness",
        "tag_desc": "Agent Harness 治理验证：异步演练、报告查询与场景模板",
        "ops": {
            "run": ("启动 Harness 演练", "异步执行预置治理步骤链，立即返回 harnessId。"),
            "getReport": ("查询 Harness 报告", "按 harnessId 查询完整演练报告。"),
            "listReports": ("分页查询 Harness 报告", "支持 status、startFrom、startTo 筛选。"),
            "getLatestReport": ("查询最新 Harness 报告", "返回最近一条演练报告。"),
            "getScenarioTemplates": ("查询 Harness 场景模板", "返回内置 QA/Chat 等演练场景模板。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/web/ModelConnectivityController.java",
        "tag": "Runtime - 模型",
        "tag_desc": "模型连通性检查、状态总览与熔断治理查询",
        "ops": {
            "check": ("检查模型连通性", "对指定 modelCode 发起连通性探测并返回检查结果。"),
            "listStatuses": ("查询模型状态列表", "按 providerCode/modelCode 筛选模型运行状态摘要。"),
            "getOverview": ("查询模型状态总览", "聚合成功/失败次数、最近检查时间与失败摘要。"),
            "listGovernanceStates": ("查询模型治理状态", "返回熔断状态、连续失败次数与降级模型信息。"),
            "refreshStatuses": ("刷新模型状态", "批量重新检查模型连通性并更新状态。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/observation/web/RuntimeObservationController.java",
        "tag": "Runtime - 观测",
        "tag_desc": "执行记录、用量、Trace Span、审计日志与统计聚合",
        "ops": {
            "listExecutions": ("分页查询执行记录", "支持 traceId、capabilityCode、时间窗口与 sort 排序。"),
            "getExecutionDetail": ("查询执行链路详情", "按 traceId 返回 input/routing/result 及关联 usages。"),
            "listUsages": ("分页查询用量记录", "支持 traceId、capabilityCode、时间窗口筛选。"),
            "listModelChecks": ("分页查询模型检查记录", "查询历史模型连通性检查记录。"),
            "getLatestModelCheck": ("查询最近模型检查", "返回最新一条模型检查记录。"),
            "aggregateStats": ("聚合观测统计", "按能力/模型/Tool 维度聚合调用统计。"),
            "listTraceSpans": ("查询 Trace Span 步骤树", "按 traceId 返回 AGENT/TOOL/LLM 等步骤树。"),
            "listAuditLogs": ("分页查询审计日志", "查询配置变更与运行时审计事件。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/usage/web/RuntimeUsageAccountController.java",
        "tag": "Runtime - 用量额度",
        "tag_desc": "租户额度账户查询",
        "ops": {
            "currentAccount": ("查询当前租户额度账户", "返回余额、预检成本等额度账户信息。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/session/web/RuntimeSessionController.java",
        "tag": "Runtime - 会话",
        "tag_desc": "多轮会话上下文管理",
        "ops": {
            "createSession": ("创建会话", "创建新的对话会话，返回 sessionId。"),
            "getSession": ("查询会话详情", "按 sessionId 查询会话元数据。"),
            "listMessages": ("查询会话消息", "按 sessionId 分页查询历史消息。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/feedback/web/RuntimeFeedbackController.java",
        "tag": "Runtime - 反馈",
        "tag_desc": "执行结果反馈闭环",
        "ops": {
            "submitFeedback": ("提交执行反馈", "对某次 traceId 执行提交评分或文字反馈。"),
            "listFeedback": ("查询反馈列表", "分页查询历史反馈记录。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/knowledge/web/RuntimeKnowledgeController.java",
        "tag": "Runtime - 知识库",
        "tag_desc": "知识片段、场景绑定与检索",
        "ops": {
            "createFragment": ("创建知识片段", "写入知识库片段内容。"),
            "listFragments": ("查询知识片段列表", "分页查询知识片段。"),
            "createBinding": ("创建场景知识绑定", "将知识片段绑定到 scenarioCode。"),
            "listBindings": ("查询场景绑定列表", "查询知识场景绑定关系。"),
            "retrieve": ("检索知识片段", "按 scenario/query 检索相关知识，供 Prompt 注入。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/file/web/RuntimeFileController.java",
        "tag": "Runtime - 文件",
        "tag_desc": "文件上传元数据与解析任务",
        "ops": {
            "registerUpload": ("注册文件上传", "登记文件元数据，返回 fileId。"),
            "getUpload": ("查询文件上传记录", "按 fileId 查询文件元数据。"),
            "startParse": ("启动文件解析", "触发文件内容解析任务。"),
            "getLatestParseResult": ("查询文件解析结果", "按 fileId 获取最新解析结果。"),
        },
    },
    {
        "path": "cognitive-enhancement-ai-runtime/src/main/java/cn/cyc/ai/cog/runtime/tool/web/ToolDebugController.java",
        "tag": "Runtime - Tool 调试",
        "tag_desc": "Tool 管理页试调用入口，绕过完整 Capability 链路",
        "ops": {
            "debugInvoke": ("调试调用 Tool", "按 toolCode 直接试调用 Tool，返回 invocationResult。用于 Tool 配置页验真。"),
        },
    },
]


def ensure_imports(content: str, need_public: bool) -> str:
    if "import io.swagger.v3.oas.annotations.Operation;" in content:
        return content
    anchor = "import org.springframework"
    idx = content.find(anchor)
    if idx == -1:
        raise ValueError("cannot find import anchor")
    block = IMPORTS + (PUBLIC_IMPORT if need_public else "")
    return content[:idx] + block + content[idx:]


def ensure_tag(content: str, tag: str, tag_desc: str) -> str:
    if "@Tag(" in content:
        return content
    return content.replace(
        "@RestController\n",
        f'@Tag(name = "{tag}", description = "{tag_desc}")\n@RestController\n',
        1,
    )


def annotate_method(content: str, method: str, summary: str, description: str, public: bool) -> str:
    pattern = re.compile(
        rf"(?P<indent>[ \t]*)@(?P<ann>PostMapping|GetMapping|PutMapping|DeleteMapping|PatchMapping)(?P<mapping>\([^\)]*\))\n"
        rf"(?P=indent)public ",
        re.MULTILINE,
    )

    def replacer(match: re.Match[str]) -> str:
        start = match.start()
        prefix = content[max(0, start - 400):start]
        if f"public " in prefix and summary in prefix:
            return match.group(0)
        # find method name after this mapping
        after = content[match.end() - len(f"{match.group('indent')}public "):]
        name_match = re.match(rf"{re.escape(match.group('indent'))}public [\w<>,\s\[\]?]+\s+{method}\s*\(", after)
        if not name_match:
            return match.group(0)
        security = f'{match.group("indent")}@SecurityRequirements()\n' if public else ""
        op = (
            f'{match.group("indent")}@Operation(summary = "{summary}", description = "{description}")\n'
            f"{security}"
            f'{match.group("indent")}@{match.group("ann")}{match.group("mapping")}\n'
            f'{match.group("indent")}public '
        )
        return op

    # method-specific: only annotate when method name follows
    lines = content.splitlines(keepends=True)
    out: list[str] = []
    i = 0
    while i < len(lines):
        line = lines[i]
        mapping_match = re.match(r"(\s*)@(PostMapping|GetMapping|PutMapping|DeleteMapping|PatchMapping)(\(.*\))\s*\n?", line)
        if mapping_match and i + 1 < len(lines):
            next_line = lines[i + 1]
            method_match = re.match(rf"{re.escape(mapping_match.group(1))}public .*\s{method}\s*\(", next_line)
            if method_match and "@Operation" not in (out[-1] if out else ""):
                indent = mapping_match.group(1)
                if f'@Operation(summary = "{summary}"' not in "".join(out[-3:]):
                    out.append(f'{indent}@Operation(summary = "{summary}", description = "{description}")\n')
                    if public:
                        out.append(f"{indent}@SecurityRequirements()\n")
        out.append(line)
        i += 1
    return "".join(out)


def process(meta: dict) -> None:
    path = ROOT / meta["path"]
    content = path.read_text(encoding="utf-8")
    public_methods = set(meta.get("public_methods", []))
    if meta.get("public"):
        public_methods |= set(meta["ops"].keys())
    need_public_import = bool(public_methods)
    content = ensure_imports(content, need_public_import)
    content = ensure_tag(content, meta["tag"], meta["tag_desc"])
    for method, (summary, description) in meta["ops"].items():
        content = annotate_method(content, method, summary, description, method in public_methods)
    path.write_text(content, encoding="utf-8")
    print(f"annotated: {meta['path']}")


def main() -> None:
    for meta in CONTROLLERS:
        process(meta)


if __name__ == "__main__":
    main()
