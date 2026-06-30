# 后端任务清单

> **仓库：** `CognitiveEnhancementJAi`  
> **配对文档：** 前端 [`CognitiveEnhancementJAiView/docs/frontend-task-checklist.md`](../../CognitiveEnhancementJAiView/docs/frontend-task-checklist.md)  
> **创建日期：** 2026-06-09  
> **用途：** 后续后端迭代按本清单推进；完成一项勾选一项，并补充验证命令与备注。

---

## 状态说明

- `[ ]` 未完成
- `[x]` 已完成
- `[~]` 进行中 / 部分完成（在备注中说明）

**优先级：** P0 联调阻塞 → P1 持久化与真实接入 → P2 鉴权与 Tool → P3 统计与开放 → PH2+ 平台二～五期

**验证约定：** 行为变更需补/改测试；合并前在仓库根目录执行 `./mvnw test`（或注明定向测试命令）。

---

## 一、已完成基线（勿重复开发）

- [x] 多模块 Maven 骨架（api / core / center / runtime / starter / sdk）
- [x] Capability → Agent → LLM/Tool 主链路
- [x] Input / Output Schema 运行时校验（BE-P0-02）
- [x] LLM / Tool 响应协议统一：`executorType` / `invocationResult`（BE-P0-03）
- [x] 观测列表分页排序 API：`executions` / `usages` / `model-checks`（BE-P0-04）
- [x] 模型检查与状态接口（check / statuses / overview / refresh）
- [x] 百炼 provider 骨架（`qwen-plus`，抽象接通）
- [x] 元数据 MySQL 持久化：Model / Prompt / Capability / Agent / Skill / Tool（NEW-PERSIST-01 / BE-P4-01）
- [x] ModelCheckRecord 持久化（NEW-PERSIST-02）
- [x] Agent Harness 一期：5 治理接口 + 8 验证步骤 + REST API
- [x] Harness 二期后端：MyBatis Plus 报告持久化 + WebSocket `/ws/harness` + 引擎步骤回调 + 报告分页
- [x] JWT 用户体系骨架：`AuthController` / `UserController` / V4 迁移

---

## 二、P0 — 联调阻塞 / 契约收口

| ID | 勾选 | 任务 | 具体工作 | 依赖前端 |
|----|:----:|------|----------|----------|
| B-P0-01 | [x] | 修复 starter 集成测试 | `AdminUserInitializer` 加 `@ConditionalOnProperty`（测试关闭持久化时不再访问 qz_iam_user）；`GlobalExceptionHandlerTest` 切片排除 `JwtAuthenticationFilter`。`./mvnw test` 全绿（36/0/0） | — |
| B-P0-02 | [x] | Demo 种子数据（BE-P0-05） | `CenterDemoDataInitializer`（内存/无持久化，isEmpty 幂等守卫）与 V3 SQL（生产 DB，INSERT IGNORE）双路径覆盖 6 类元数据 + 关系表，内容完全对齐 | F-P0-02 |
| B-P0-03 | [x] | Runtime 查询面收口（BE-P0-01） | 模型状态侧字段齐全；`ExecutionRecord` 增 `failureReason` 字段；`ExecutionRecorder` 增 `recordFailure`；`DefaultCapabilityRuntime` try/catch 捕获 Agent 执行异常落库后照常抛出（API 响应语义不变）。新增 `LoggingExecutionRecorderTest` | F-P0-03 |
| B-P0-04 | [x] | 模型状态失败聚合（BE-P3-03） | `ModelRuntimeQueryService.buildFailureSummaries` 按失败原因分组聚合（count/最近发生时间/受影响模型）；overview 含 `failureSummaries` + 最近成功/失败时间；statuses 含 `consecutiveFailureCount` | F-P0-03、F-P1-01 |
| B-P0-05 | [x] | 观测分页契约 | `page` / `size` / `sort` / `totalPages` / `hasNext` 已提供 | F-P0-04 |

---

## 三、P1 — 持久化收尾 + 真实模型

| ID | 勾选 | 任务 | 具体工作 | 依赖前端 |
|----|:----:|------|----------|----------|
| B-P1-01 | [x] | ExecutionRecord 落库（NEW-PERSIST-03） | V5 迁移 `qz_rt_execution_record` + Entity/Mapper + `PersistentExecutionRecordRepository`（含 failureReason）；与 InMemory 按 `cog.persistence.enabled` 互斥；新增映射单测 | F-P0-04 |
| B-P1-02 | [x] | UsageRecord 落库（NEW-PERSIST-03） | V6 迁移 `qz_rt_usage_record` + Entity/Mapper + `PersistentUsageRecordRepository`；新增映射单测 | F-P1-10 |
| B-P1-03 | [x] | 仓储切换策略（BE-P4-03） | 全 runtime 仓储统一 `@ConditionalOnProperty(cog.persistence.enabled)`：InMemory `false`+matchIfMissing、Persistent `true`，各取值恰一个 bean；测试默认内存、生产默认 DB（主配置 enabled=true + flyway 自动扫描 V1~V6） | — |
| B-P1-04 | [x] | 初始数据与迁移（BE-P4-04） | 统一策略：DB 模式 Flyway V2~V6（`spring.flyway.enabled=${cog.persistence.enabled}`）+ `AdminUserInitializer`；内存模式 `CenterDemoDataInitializer`；总开关 `cog.seed.enabled`；`FlywayRepairConfig` 仅持久化模式；新增 `CogSeedProperties` + `CenterSeedStrategyTest` | F-P0-02 |
| B-P1-05 | [x] | 百炼真实 smoke check（BE-P1-01） | `DashscopeProperties.isApiKeyConfigured()` + 占位符 `__DASHSCOPE_API_KEY__`；`BailianLlmProviderHandler` 无密钥 mock 降级（`mock=true`）、有密钥真实 HTTP；`BailianModelConnectivitySmokeTest` 仅在有 `DASHSCOPE_API_KEY` 时执行；新增 `BailianLlmProviderHandlerTest` | F-P1-04 |
| B-P1-06 | [x] | LLM token/usage 解析（BE-P1-03） | `LlmInvocationResult` 增 token/latency 字段；`OpenAiCompatibleUsageParser` 解析 `usage.prompt_tokens/completion_tokens/total_tokens`；`BailianLlmProviderHandler` 写入；`LoggingUsageMeter` 落 `UsageRecord`；新增 `OpenAiCompatibleUsageParserTest` / `LoggingUsageMeterTest` | F-P1-10 |
| B-P1-07 | [x] | 模型检查可选真实测试（BE-P1-04） | `@Tag(smoke)` + 根 POM Surefire 默认 `excludedGroups=smoke`；`-Psmoke` 仅跑 smoke；`@EnabledIfEnvironmentVariable(DASHSCOPE_API_KEY)` 双重保护；覆盖 check + refresh + statuses 真实链路 | — |
| B-P1-08 | [x] | Harness 报告筛选 API | `GET /api/admin/harness/reports` 支持 `status` / `startFrom` / `startTo`（ISO-8601）；`HarnessReportQuery` + 内存/DB 双仓储筛选；新增 `InMemoryHarnessReportRepositoryTest` | F-P1-09 |
| B-P1-09 | [x] | WebSocket CANCEL 可中断 | `HarnessCancellation` + 引擎跳步 SKIPPED/CANCELLED；WS 仅设 token 不 interrupt | — |

---

## 四、P2 — 鉴权 + Tool + 观测增强

| ID | 勾选 | 任务 | 具体工作 | 依赖前端 |
|----|:----:|------|----------|----------|
| B-P2-01 | [x] | 全接口鉴权打通（BE-P5-01） | `JwtProperties.authEnabled` + `permitAll` 白名单；Filter 未授权 401；测试 `auth-enabled: false` | F-P2-01 ~ F-P2-04 |
| B-P2-02 | [x] | 用户管理 API 完善 | `GET /api/users` 分页 + `ADMIN` 角色校验（`AuthSupport`）；403/401 映射 | F-P2-02 |
| B-P2-03 | [x] | 执行记录链路详情（BE-P3-01） | V7 增 input/routing/result JSON；`GET /api/runtime/observations/executions/{traceId}` 含 usages | F-P2-05 |
| B-P2-04 | [x] | HTTP Tool 接入（BE-P2-01） | `ToolHttpExecutor` + `DefaultToolHttpExecutor`；`MockToolRuntime` 支持 HTTP 协议（implRef URL/JSON）；`mock=false` 落观测 | F-P2-08 |
| B-P2-05 | [x] | Tool 参数 Schema 校验（BE-P2-03） | `ToolInputSchemaValidator` 执行前校验必填与类型；JAVA_LOCAL/HTTP 统一拦截 | — |
| B-P2-06 | [x] | 统一时间窗口查询（BE-P3-04） | executions / usages / model-checks 支持 `startTime` / `endTime`（ISO-8601 闭区间） | F-P2-06 |
| B-P2-07 | [x] | Harness 集成测试 | 新增 `HarnessControllerTest`：模板 / 异步 run / 报告查询 / 筛选 | — |
| B-P2-08 | [x] | Harness 持久化仓储完善 | `PersistentHarnessReportRepository.toDomain` 还原 scenario/summary/steps JSON | F-P1-05 |

---

## 五、P3 — 统计 + Tool 治理 + 开放

| ID | 勾选 | 任务 | 具体工作 | 依赖前端 |
|----|:----:|------|----------|----------|
| B-P3-01 | [x] | 观测聚合统计（BE-P3-02） | `GET /api/runtime/observations/stats`：summary + byCapability/byModel/byTool；支持 `startTime`/`endTime` | F-P3-01 |
| B-P3-02 | [x] | MCP Tool 骨架（BE-P2-02） | `McpToolClient` + `LocalMcpToolClient`（demoEcho）；`MockToolRuntime` 支持 MCP 协议 | F-PH4-01 |
| B-P3-03 | [x] | Tool 权限/超时/重试（BE-P2-04） | `DefaultToolRuntime` 按当前上下文已加载 Skill 绑定关系校验 Tool 调用权限；HTTP Tool 透传 `timeoutMs`；统一按 `RetryPolicy.maxAttempts` 重试 Tool 调用（至少执行 1 次）；新增权限、重试、超时定向测试 | — |
| B-P3-04 | [x] | Tool 审计与风险等级（BE-P2-05） | Tool 元数据新增 `riskLevel`；管理端 DTO/Service/DB 映射同步；V8 迁移为已有工具补默认 `LOW`；运行时 `ToolInvocationResult` 暴露风险等级，高风险 Tool 可识别。完整审计日志留给 B-P3-09 | — |
| B-P3-05 | [x] | OpenAI-compatible 抽象（BE-P1-02） | 新增 `OpenAiCompatibleChatClient` 统一 Chat Completions URL 规范化、请求体构造、HTTP 调用、choices/message/content 与 usage 解析；百炼处理器复用该模板，保留 provider 凭证解析与 mock 降级 | — |
| B-P3-06 | [x] | 多租户隔离（BE-P5-02） | JWT 与请求上下文透传 `tenantCode`；Center 元数据仓储按租户过滤与写入；Runtime 观测记录落库、查询与聚合按租户隔离；V9 迁移补齐默认租户 `default` | — |
| B-P3-07 | [x] | 限流与配额（BE-P5-03） | `RuntimeQuotaLimiter` + `DefaultRuntimeQuotaLimiter`：按租户隔离的应用级 / 能力级每分钟滑动窗口限流；`cog.runtime.quota.*` 配置默认关闭；超限返回 `TOO_MANY_REQUESTS` / HTTP 429 | F-PH2-01 |
| B-P3-08 | [x] | 高风险能力治理（BE-P5-04） | `PolicyHarness` 对 `HIGH` / `needHumanConfirm` 能力执行前拦截，要求 `humanConfirmed=true`；`RuntimeHarness` 剥离治理参数后下发执行；`OutputGovernance` 为高风险输出追加 `PENDING_REVIEW` 审查标记，并提供日志敏感字段脱敏 | F-PH2-06 |
| B-P3-09 | [x] | 审计日志（BE-P5-05） | 新增 runtime audit 子域：配置变更 CREATE/UPDATE/SEED 与运行调用成功/失败审计；内存/持久化双仓储 + V10 `qz_rt_audit_log` 迁移；运行入口集成审计断言 | F-PH2-05 |
| B-P3-10 | [x] | 开放 API 文档化（BE-P6-01） | 新增 `docs/openapi.yaml` 静态 OpenAPI 3.0 文档与 `docs/api-reference.md` 联调说明；覆盖鉴权/租户/Trace、Runtime 执行、观测统计、模型检查、Harness、Center 元数据接口；新增文档交付校验测试 | — |
| B-P3-11 | [x] | SDK 第一版（BE-P6-02） | `cognitive-enhancement-ai-sdk` 新增同步 Java 客户端：`CogSdkClient` / `CogSdkClientConfig` / `CapabilityExecutionRequest` / `CapabilityExecutionResult` / `CogSdkException`；封装 Runtime 能力执行、JWT/租户/Trace 透传与统一错误映射；新增 SDK 快速接入文档和客户端测试 | — |
| B-P3-12 | [x] | 流式/异步接口（BE-P6-03） | 新增 `POST /api/runtime/capabilities/execute/stream` SSE 执行入口；事件含 `STARTED` / `COMPLETED` / `FAILED`；复用同步 RuntimeHarness 链路与 Trace/租户/限流/治理/观测/审计；异步任务 job API 留作后续扩展 | F-P3-02 |
| B-P3-13 | [x] | 错误码与排障指南（BE-P6-04） | `docs/api-reference.md` 补齐错误码/HTTP 状态映射、Trace 透传规范、Trace 排障流程、SSE `FAILED` 事件处理规则；文档测试覆盖关键片段 | — |

---

## 六、平台二期 — 治理运营期（PH2）

> 参考：  
> - `docs/superpowers/specs/2026-05-11-platform-phased-detailed-design.md` 第 4 节  
> - `docs/superpowers/specs/2026-06-14-phase-2-governance-design.md`（二期详细设计）

### 6.1 管理台联调前置（S0，优先于 PH2 治理子域）

| ID | 勾选 | 任务 | 具体工作 | 依赖前端 |
|----|:----:|------|----------|----------|
| B-PH2-00a | [x] | Center 统一列表查询 | 六类 `GET /api/center/{resource}` 支持 `page/size/sort/keyword/status` + 资源专属 filter；响应 `CenterPageResult`（对齐 Runtime 分页契约）；OpenAPI 更新 | F-Center 列表筛选 |
| B-AI-00 | [x] | 模型提供商 + 模型 M:N | V38 三表（`qz_ai_model_provider` / `qz_ai_model` / `qz_ai_model_provider_binding`）；`/api/center/model-providers` CRUD；`ModelUpsertRequest.providerBindings`；`CatalogModelDefinitionRepository` 运行时展开 | F-AI-00 |
| B-AI-01 | [x] | API Key AES 加密存储 | V39 `api_key` 列；`ApiKeyProtector` / `AesApiKeyProtector`（`enc:v1:`）；`cog.crypto.*` 配置；CMS 统一填 Key，禁止环境变量 | F-AI-00 |
| B-AI-02 | [x] | LLM 路由启动预注册 | `LlmRouteRegistry` + `ModelRuntimeBootstrap` + Admin 变更后 `ModelRuntimeRefreshService.refresh()` | — |
| B-AI-03 | [x] | ReAct 多轮 Tool 对话 | `ReActAgentExecutor` + `LlmGateway.chat()`；`reactEnabled` / `reactMaxIterations` 请求参数 | F-AI-07 |
| B-PH2-00b | [x] | Tool 调试调用 | 新增 `POST /api/runtime/tools/{toolCode}/debug-invoke`：复用 `ToolRuntime` 协议执行与 Schema 校验，不校验 Skill 绑定；HIGH 风险支持 `debugConfirmed` / `X-Debug-Confirmed`；成功/失败审计 `TOOL_DEBUG_INVOKE`；补集成测试与 OpenAPI/API reference | F-Tool 调试入口 |

### 6.2 治理运营子域（S1~S7）

| ID | 勾选 | 任务 | 验收要点 |
|----|:----:|------|----------|
| B-PH2-01 | [x] | 用量与额度治理 | 租户额度账户、调用前余额拦截、调用后按用量成本扣减、内存/DB 仓储、查询 API |
| B-PH2-02 | [x] | Prompt 发布与灰度 | 草稿 / 发布 / 版本切换 |
| B-PH2-03 | [x] | Capability 发布与版本控制 | 版本、灰度开关、租户启停 |
| B-PH2-04 | [x] | 模型治理增强 | 降级 / 熔断 / 请求级超时运行时生效 |
| B-PH2-05 | [x] | Trace 与审计增强 | 统一流水、工具日志、异常栈追踪 |
| B-PH2-06 | [x] | 子域包结构 | `usage` / `release` / `audit` / `policy`（可先包内拆分） |

### 6.3 当前剩余功能清单

**PH2 治理运营期（S0~S7）已全部收口。**

| 优先级 | ID | 状态 | 剩余工作 |
|--------|----|------|----------|
| PH3-S1 | B-PH3-02 | 已完成 | Skill 依赖解析、OutputGovernance 增强、包迁移 |
| PH3-S2 | B-PH3-01 | 已完成 | session/feedback + knowledge/file 子域、V15/V16 迁移、执行链路上下文注入 |
| PH4-S1 | B-PH4-01 | 已完成 | Tool Adapter 注册表、HTTP MCP 客户端、TraceSpan OTLP 导出桥 |
| PH4-S2 | B-PH4-02 | 已完成 | PolicyHarness 统一 RBAC/限流/额度/熔断预检/灰度版本解析 |
| PH5-S1 | B-PH5-01 | 已完成 | 任务规划、多 Agent 委派、循环防护、任务预算控制 |
| PH5-S2 | B-PH5-02 | 已完成 | LLM 规划、自反思重试、策略化模型选择、多 Agent 结果合并 |
| PH5-S3 | B-PH5-03 | 已完成 | 计划驱动 Tool 选择、PH5 MockMvc 集成测试、前端联调说明 |

---

## 七、三～五期路线图（PH3 ~ PH5）

| ID | 勾选 | 期 | 任务 |
|----|:----:|----|------|
| B-PH3-01 | [x] | 三期 | 知识库 / 文件 / 会话 / 反馈 |
| B-PH3-02 | [x] | 三期 | SkillLoader / OutputGovernance 增强 |
| B-PH4-01 | [x] | 四期 | MCP 适配 / Tool Adapter / OpenTelemetry |
| B-PH4-02 | [x] | 四期 | PolicyHarness 额度 / RBAC / 灰度 / 熔断 |
| B-PH5-01 | [x] | 五期 | 多 Agent / 规划 / 自反思 / 成本策略 |
| B-PH5-02 | [x] | 五期 | LLM 规划 / 自反思重试 / 策略化模型选择 / 结果合并 |
| B-PH5-03 | [x] | 五期 | 计划驱动 Tool 选择 / PH5 集成测试 / 前端联调契约 |

---

## 十、启知 CMS + 三进程鉴权（进行中）

> 配对前端 CMS：`CognitiveEnhancementJAiView/packages/cms`  
> 生产部署：[`docs/cms-production-deployment.md`](./cms-production-deployment.md)

### 10.1 CMS 横切

| ID | 勾选 | 任务 | 状态 |
|----|:----:|------|------|
| B-CMS-00 | [x] | admin 模块 + starter 集成 | ✅ |
| B-CMS-01~07 | [x] | RBAC / 上下文 / /me / Flyway / App API / Runtime 额度 / prod profile | ✅ |
| B-CMS-08 | [x] | CMS 冒烟集成测试（7 模块） | ✅ |
| B-CMS-09 | [x] | CMS 全模块集成测试 | ✅ 8 个 IT 类 + AppOrderFlow |
| B-CMS-10 | [x] | OpenAPI 同步 Admin 接口 | ✅ 静态 openapi + springdoc/Knife4j |
| B-CMS-11 | [x] | 本清单第十节 | ✅ |

### 10.2 CMS 业务域（API 已实现，测试见 B-CMS-09）

| 模块 | 勾选 | 说明 |
|------|:----:|------|
| 账号治理 | [x] | users / roles / permissions / tenants / orgs / security-config |
| 会员体系 | [x] | levels / members / change-logs / **benefit_def + level_benefit**（V30） |
| 套餐计费 | [x] | packages / orders / quota / lifecycle Job；**订阅套餐扩展列**（V31）；**支付回调占位验签**（MOCK/WECHAT/ALIPAY） |
| 知识内容 | [x] | contents / tags / knowledge-packages / import-jobs |
| 数据运营 | [x] | dashboard + AI 分布/成本趋势 |
| 系统设置 | [x] | dicts / features / audit-logs / health；**Caffeine L1 本地缓存**（5min TTL + 写失效） |
| AI 成本看板 | [x] | `GET /api/admin/ai/cost-dashboard` |
| AI 路由治理 | [x] | `GET /api/admin/ai/routing-overview` |

### 10.3 三进程鉴权

| ID | 勾选 | 任务 | 状态 |
|----|:----:|------|------|
| B-AUTH-01~08 | [x] | common + Auth + Gateway + OAuth2 JDBC + IT | ✅ |
| B-AUTH-09 | [x] | 网关头 /me 集成测试 | ✅ |
| B-AUTH-10 | [x] | 生产方案 B（gateway api-auth + trust headers） | ✅ |
| B-AUTH-11~12 | [x] | E2E 脚本 + 部署文档 | ✅ |

### 10.4 待办（不含支付）

| ID | 勾选 | 任务 |
|----|:----:|------|
| B-CMS-5.a | [x] | Center 元数据 MySQL 持久化集成测试 |
| B-CMS-5.b | [x] | AI 路由/治理只读 Admin API |
| B-CMS-10 | [x] | `docs/openapi.yaml` 静态 Admin 路径补全 |

**CMS 集成测试命令：**

```bash
./mvnw -pl cognitive-enhancement-ai-admin-server test \
  '-Dtest=Admin*IntegrationTest,CenterMetadataPersistenceIntegrationTest,AppOrderFlowControllerTest,Knife4jOpenApiIntegrationTest,OpenApiDocumentTest,AdminAuthMeWithTrustGatewayHeadersIntegrationTest,AdminWorkbenchRoleIntegrationTest,AdminBillingLifecycleIntegrationTest' \
  -Dsurefire.failIfNoSpecifiedTests=false
```

> 勿对 `-am` 依赖模块附带 `-Dtest=...`：会在无 JUnit 引擎的 `common` 等模块误跑 Surefire 导致失败。

---

## 十一、平台分层迁移（P0 收尾）

> 详见 `docs/platform-architecture.md` §6。

| ID | 勾选 | 任务 | 状态 |
|----|:----:|------|------|
| B-ARCH-P0-01 | [x] | platform 抽层 + repository 化 + ArchUnit | ✅ |
| B-ARCH-P0-02 | [x] | admin-server / app-server 双启动 + 网关 `/api/app/**` | ✅ |
| B-ARCH-P0-03 | [x] | DB `qz_` 前缀统一（V25 + IT schema） | ✅ |
| B-ARCH-P0-04 | [x] | IT 加固：`CenterMetadata` 关闭调度；TrustGateway 测试禁用 JWKS 拉取 | ✅ |
| B-ARCH-P0-05 | [x] | 移除遗留 `cognitive-enhancement-ai-starter` 模块目录 | ✅ |
| B-ARCH-P0-06 | [x] | 更新 `platform-architecture.md` 落地状态 | ✅ |

**P1 待办（下一迭代）：** ~~工作台 Todo、计费超时关闭、RBAC repository 化~~ 已完成；Phase 3 CMS 收尾见 V28 迁移与下方变更记录。

## 十二、Phase 3 CMS 业务收尾（2026-06-23）

| ID | 勾选 | 任务 | 状态 |
|----|:----:|------|------|
| B-PH3-CMS-01 | [x] | `workbench:view` 权限种子（V28） | ✅ |
| B-PH3-CMS-02 | [x] | app-server 知识库/学习/运营 IT | ✅ `AppKnowledgeOpsIntegrationTest` |
| B-PH3-CMS-03 | [x] | 内容版本快照与回滚 | ✅ `qz_kb_content_version` + `/versions` `/rollback` |
| B-PH3-CMS-04 | [x] | C 端 `/api/app/ops/*` + `AnnouncementScheduleJob` | ✅ |
| B-PH3-CMS-05 | [x] | 手机/邮箱注册单测 + 封禁吊销 OAuth2 令牌 | ✅ |
| B-PH3-CMS-06 | [x] | OpenAPI / api-reference / 本清单同步 | ✅ |

**增强测试（2026-06-23）：**

| 场景 | 测试类 |
|------|--------|
| 审核发布写版本快照 + 回滚 | `AdminContentIntegrationTest` |
| 公告定时发布仓储逻辑 | `AdminOperationIntegrationTest` + `AnnouncementScheduleJobTest` |
| 工作台 2A 角色化首页 | `WorkbenchPersonalizationServiceTest` + `AdminCmsModulesIntegrationTest` |

**验证命令：**

```bash
./mvnw -pl cognitive-enhancement-ai-platform test -Dtest=IamAuthServiceTest,UserAdminServiceBanTest,ContentServiceVersionTest
./mvnw -pl cognitive-enhancement-ai-app-server test -Dtest=AppKnowledgeOpsIntegrationTest,AppLearningInvokeIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false
```

---

## 十三、CMS 业务增强 P1（2026-06-24）

> 对应各 `module-design-*.md` §10 现状列同步；Flyway **V29~V31**（admin-server + app-server 双份）。

| ID | 勾选 | 任务 | 备注 |
|----|:----:|------|------|
| B-CMS-P1-01 | [x] | V29 内置角色权限种子 | OPERATOR / CONTENT / SUPPORT + 全角色 `workbench:view` |
| B-CMS-P1-02 | [x] | 多角色工作台 IT | `AdminWorkbenchRoleIntegrationTest`（网关头 + `cog.persistence.enabled=false`） |
| B-CMS-P1-03 | [x] | V30 会员权益目录 | `qz_mbr_benefit_def` + `qz_mbr_level_benefit`；`MembershipBenefitSupport` 优先读表 |
| B-CMS-P1-04 | [x] | 系统设置 Caffeine 缓存 | `PlatformLocalCache`；Dict / FeatureSwitch / SecurityConfig 读缓存 + 写失效 |
| B-CMS-P1-05 | [x] | V31 订阅套餐扩展列 | `daily_limit` / `concurrent_limit` / `model_scope` |
| B-CMS-P1-06 | [x] | 计费生命周期 IT | `AdminBillingLifecycleIntegrationTest`（超时关单 + 内容导入 Job） |
| B-CMS-P1-07 | [x] | 内容列表排除 body | `DbContentRepository.page` 投影优化 |
| B-CMS-P1-08 | [x] | C 端 learning invoke IT | `AppLearningInvokeIntegrationTest`（QA 模式） |

**验证命令：**

```bash
./mvnw -pl cognitive-enhancement-ai-platform test -Dtest=MembershipBenefitSupportTest,BillingLifecycleServiceTest
./mvnw -pl cognitive-enhancement-ai-admin-server test \
  -Dtest=AdminWorkbenchRoleIntegrationTest,AdminBillingLifecycleIntegrationTest,AdminContentIntegrationTest,AdminCmsModulesIntegrationTest \
  -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-app-server test \
  -Dtest=AppKnowledgeOpsIntegrationTest,AppLearningInvokeIntegrationTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

---

## 十四、CMS 业务增强 P2（已完成）

> 设计基线：`module-design-billing.md` §6.6、`module-design-operations.md` §6.3、`module-design-account.md` §2.6。

| ID | 勾选 | 任务 | 备注 |
|----|:----:|------|------|
| B-CMS-P2-01 | [x] | 真实支付回调链路 | `AppPaymentCallbackIntegrationTest`（下单 → MOCK 验签 → 履约） |
| B-CMS-P2-02 | [x] | 消息触达 SPI | `MessageTemplateRenderer` + `MessageSender`/`DefaultNoopMessageSender` + `POST .../preview` |
| B-CMS-P2-03 | [x] | 组织 2B/2G 开户 IT | `AdminOrgIntegrationTest`；修正 IT schema `qz_acct_org_member.org_role` |
| B-CMS-P2-04 | [x] | 客服工单域 | V32 `qz_ops_support_ticket` + Admin CRUD + 工作台 SUPPORT 待办 + `AdminSupportTicketIntegrationTest` |
| B-CMS-P2-05 | [x] | 公告定向 | V33 `target_level_codes`/`target_user_ids` + C 端过滤 + `AnnouncementAudienceSupportTest`/`AppAnnouncementTargetingIntegrationTest` |
| B-CMS-P2-06 | [x] | 系统设置 Redis L2 | `PlatformLocalCache` Caffeine L1 + 可选 Redis L2 + Pub/Sub L1 广播失效 |

**P2 验证命令（随项补充）：**

```bash
./mvnw -pl cognitive-enhancement-ai-app-server test -Dtest=AppPaymentCallbackIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-platform test -Dtest=MessageTemplateRendererTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-admin-server test -Dtest=AdminOrgIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-admin-server test -Dtest=AdminSupportTicketIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-admin test -Dtest=WorkbenchPersonalizationServiceTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-platform test -Dtest=AnnouncementAudienceSupportTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-app-server test -Dtest=AppAnnouncementTargetingIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-platform test -Dtest=PlatformLocalCacheTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw clean test
```

---

## 十五、CMS 业务增强 P3（已完成）

> 设计基线：`module-design-operations.md` §6.3、`module-design-billing.md` §6.6、`module-design-account.md` §2.6、`module-design-membership.md` §2.8、`module-design-knowledge.md` K5、`module-design-system.md` §9。  
> **前置：** CMS P2 已收口，`./mvnw clean test` 全绿。

| ID | 勾选 | 任务 | 备注 |
|----|:----:|------|------|
| B-CMS-P3-01 | [x] | 消息实发通道 | `MessageSender` 接 SMS / EMAIL / IN_APP 至少一种真实实现；保留 `DefaultNoopMessageSender`；模板 `render` + 发送审计 |
| B-CMS-P3-02 | [x] | 微信/支付宝正式对接 | `PaymentChannelGateway` + canonical 验签（微信 V3 RSA / 支付宝 RSA2）；`PaymentChannelSignatureVerifierTest` |
| B-CMS-P3-03 | [x] | C 端发起真实支付 | `POST /api/app/billing/orders/{id}/pay` 返回 `clientParams` 预下单；回调闭环 markPaid |
| B-CMS-P3-04 | [x] | 组织 2B/2G 产品化 | 部门树 CRUD、成员邀请/角色、组织级账户视图；在 `createOrganization` + `AdminOrgIntegrationTest` 基础上扩展 |
| B-CMS-P3-05 | [x] | 企业成员额度分配 | `qz_mbr_quota_member_alloc` + `GET/POST .../member-allocs`；组织 OWNER/ADMIN 分配子成员额度 |
| B-CMS-P3-06 | [x] | Redis L2 生产验证 | `cog.platform.cache.redis-enabled=true` 多实例 IT：L2 命中 + Pub/Sub L1 失效广播 |
| B-CMS-P3-07 | [x] | 内容导入 CSV 完整解析 | `ContentImportJobService` 逐行校验入库 + 失败明细 `resultJson`；补导入成功/部分失败 IT |
| B-CMS-P3-08 | [x] | 客服工单 C 端 | C 端提单/查单 API + 与 Admin 工单状态同步；可选消息通知（依赖 P3-01） |

**建议优先级：** P3-01 → P3-06 → P3-07 → P3-04 → P3-05 → P3-08 → **P3-02 / P3-03（支付相关，最后做）**。

**P3 验证命令（随项补充）：**

```bash
./mvnw clean test
./mvnw -pl cognitive-enhancement-ai-admin-server test -Dtest=OpenApiDocumentTest -Dsurefire.failIfNoSpecifiedTests=false
./mvnw -pl cognitive-enhancement-ai-app-server test -Dtest=AppPaymentPrepayIntegrationTest,AppPaymentCallbackIntegrationTest,AppCmsP3IntegrationTest -Dsurefire.failIfNoSpecifiedTests=false
```

---

## 十五-A、C 端 AI 学习辅导引擎 P3（已完成）

> 设计基线：学习辅导对话引擎第三阶段（画像 / 掌握度 / 错题 / 计划 / 练习 / LLM 分析 / 输出治理）。  
> 配对前端：[`docs/frontend-integration-guide.md`](./frontend-integration-guide.md) F-CMS-P07。

| ID | 勾选 | 任务 | 备注 |
|----|:----:|------|------|
| B-APP-TUTORING-P3-01 | [x] | 画像与掌握度 | V45 `qz_app_learning_profile` + Redis 缓存 + `GET /api/app/tutoring/profile` |
| B-APP-TUTORING-P3-02 | [x] | 错题本与练习推荐 | `qz_app_mistake_record` / `qz_app_practice_recommendation` + 分页/待做 API |
| B-APP-TUTORING-P3-03 | [x] | 学习计划 | `qz_app_learning_plan` + `GET /api/app/tutoring/plan/active` + Prompt 注入 |
| B-APP-TUTORING-P3-04 | [x] | LLM 分析与输出治理 | `AppTutoringLlmAnalyzer`（默认关）+ `DefaultAppTutoringOutputGovernor` + SSE `PROFILE_LOADED`/`GOVERNANCE_APPLIED` |
| B-APP-TUTORING-P3-05 | [x] | 入口收口与测试 | `learning/invoke` TUTORING 委托；单元/集成测试 + 前端文档更新 |

**验证命令：**

```bash
./mvnw -pl cognitive-enhancement-ai-app-server -am test \
  -Dtest='AppTutoring*,AppLearningInvoke*' -Dsurefire.failIfNoSpecifiedTests=false
```

---

## 十五-B、C 端 Phase B（练习 / 复习 / 今日 / 导入 / 画像 BFF）

> 配对前端：`CognitiveEnhancementJAiView/docs/cog-api-contracts/` + [`docs/phase-b-field-gap.md`](./phase-b-field-gap.md)

| ID | 勾选 | 任务 | 备注 |
|----|:----:|------|------|
| B-APP-PHASE-B-01 | [x] | M0 契约适配 | `AppPageQuery`/`AppPageVO`、`auth/me` 字段、`QuotaLabelFormatter` |
| B-APP-PHASE-B-02 | [x] | M1 练习域 | `/api/practice/**` + V46 表 + Runtime SCORING + 错题联动 |
| B-APP-PHASE-B-03 | [x] | M1 复习与今日 | `/api/app/review/**` + `GET /api/app/today` BFF |
| B-APP-PHASE-B-04 | [x] | M2 导入与辅导 SSE | `/api/app/import-tasks/**` + `AppTutoringSseAdapter` delta/done/error |
| B-APP-PHASE-B-05 | [x] | M3 画像 BFF | `GET /api/app/insights/overview`；billing/ops 字段差异见 gap 文档 |
| B-APP-PHASE-B-06 | [x] | Gateway | `/api/practice/**` → 8804 + 路由集成测试 |

**验证命令：**

```bash
./mvnw -pl cognitive-enhancement-ai-app-server -am install -DskipTests -q
./mvnw -pl cognitive-enhancement-ai-app-server test \
  -Dtest='AppAuthMeContractTest,AppPractice*IntegrationTest,AppReviewIntegrationTest,AppTodayIntegrationTest,AppImportTask*IntegrationTest,AppInsightsIntegrationTest,AppTutoring*IntegrationTest,AppWebLayerArchitectureTest'
./mvnw -pl cognitive-enhancement-ai-gateway test -Dtest='AppPracticeGatewayRouteIntegrationTest'
```

---

## 十六、平台文件模块（规划，参考 ztx3-file）

> 设计对照：[`docs/frontend-file-integration.md`](./frontend-file-integration.md) §5  
> 参考项目：`ztx3-file`（api / provider / sdk 分层；`ztx_file` 元数据 + OSS 策略 + ensure 生命周期）

| ID | 勾选 | 任务 | 备注 |
|----|:----:|------|------|
| B-FILE-00 | [x] | 模块骨架 | `base-api` Feign + `base-server` 磁盘策略 |
| B-FILE-01 | [x] | Admin/公开 HTTP 上传下载 | `/api/base/files/*` |
| B-FILE-02 | [x] | Inner Feign API | `BaseFileFeignClient` + `/api/base/files/inner/*` |
| B-FILE-03 | [ ] | 分片上传 | multipart create/chunk/complete |
| B-FILE-04 | [x] | ensure 确认 | `POST .../ensure` |
| B-FILE-05 | [ ] | Runtime 真实解析对接 | 读 base 文件对象 |
| B-FILE-06 | [ ] | Flyway + OpenAPI 全量同步 | V2 已加表 |

**首期建议：** B-FILE-00 → B-FILE-01 → B-FILE-02 → B-FILE-04 → B-FILE-05。

---

## 八、推荐迭代节奏

| 迭代 | 后端任务 ID | 目标 |
|------|-------------|------|
| 第 1 周 | B-P0-01 ~ B-P0-04 | 测试全绿 + 查询契约定稿 |
| 第 2 周 | B-P0-02、B-P1-01 ~ B-P1-02 | 种子数据 + 观测落库 |
| 第 3 周 | B-P1-05 ~ B-P1-08 | 百炼验真 + Harness 筛选 |
| 第 4 周 | B-P2-01 ~ B-P2-03 | 鉴权 + 执行详情 |
| 第 5 周+ | B-P2-04+、B-P3-01 | HTTP Tool + 聚合统计 |
| 已完成 | B-CMS-P2-01~06 | CMS P2：支付回调 IT / 消息 SPI / 组织 IT / 工单 / 公告定向 / Redis L2 |
| **当前** | **CMS P3 已全部完成** | **支付预下单 + 回调验签；生产可换官方 SDK 调预下单 API** |
| **下一迭代** | **B-FILE-00~06** | **平台文件模块（参考 ztx3-file）；前端 F-FILE-01/02 阻塞** |

---

## 九、变更记录

| 日期 | 变更 |
|------|------|
| 2026-06-09 | 初版：从 superpowers 进度文档与代码现状整理，作为后续执行清单 |
| 2026-06-09 | P0 全部收口：B-P0-01 测试全绿；B-P0-02/04/05 核对达标；B-P0-03 补齐执行失败记录（failureReason + recordFailure + 主链路 try/catch） |
| 2026-06-09 | P1 持久化：B-P1-01/02 ExecutionRecord、UsageRecord 落库（V5/V6 + Entity/Mapper + Persistent 仓储 + 单测）；B-P1-03 仓储切换策略统一收口 |
| 2026-06-09 | B-P1-08 Harness 报告筛选 API：`status` / `startFrom` / `startTo` 查询参数，内存与 MyBatis Plus 双实现 |
| 2026-06-09 | B-P1-05 百炼 smoke check：无密钥 mock 降级 + 有密钥真实调用 + 可选 smoke 测试隔离 |
| 2026-06-09 | B-P1-06 LLM token/usage 解析：百炼响应 usage 解析并写入 UsageRecord |
| 2026-06-09 | B-P1-07 模型检查可选真实测试：Surefire 排除 smoke 组 + Maven profile smoke + check/refresh 验真 |
| 2026-06-10 | B-P1-04 初始数据与迁移：Flyway/Admin/Demo 三路种子策略统一，`cog.seed.enabled` 总开关 |
| 2026-06-10 | B-P1-09 WebSocket CANCEL 可中断：`HarnessCancellation` 令牌 + 引擎 SKIPPED/CANCELLED 报告，WS 不 interrupt future |
| 2026-06-10 | B-P2-01/02 鉴权打通：`JwtAuthenticationFilter` 401 + 白名单；`AuthSupport` ADMIN 校验；测试关闭鉴权 |
| 2026-06-10 | B-P2-08 Harness 持久化 `toDomain` 补全 scenario/summary/steps |
| 2026-06-10 | B-P3-06 多租户隔离：JWT/请求上下文 tenantCode、Center 元数据租户过滤、Runtime 观测租户隔离、V9 默认租户迁移 |
| 2026-06-10 | B-P3-07 限流与配额：运行时调用前应用级 / 能力级限流，按租户隔离，超限统一映射 429 |
| 2026-06-10 | B-P3-08 高风险能力治理：高风险 / 需确认能力执行前人工确认拦截，确认后输出追加待审查标记，日志敏感字段脱敏 |
| 2026-06-10 | B-P2-03 执行链路详情：V7 JSON 字段 + `GET .../executions/{traceId}` |
| 2026-06-10 | B-P2-06 观测接口统一 `startTime`/`endTime`；B-P2-07 `HarnessControllerTest` |
| 2026-06-10 | B-P2-04 HTTP Tool：`ToolHttpExecutor` + HTTP 协议执行；B-P2-05 `ToolInputSchemaValidator` 入参校验 |
| 2026-06-10 | B-P3-01 观测聚合统计：`GET /observations/stats` 按能力/模型/Tool 聚合 + 时间窗口 |
| 2026-06-10 | B-P3-02 MCP Tool 骨架：`McpToolClient` + `LocalMcpToolClient` demoEcho |
| 2026-06-10 | B-P3-03 Tool 权限/超时/重试：运行时校验 Skill 绑定授权、HTTP 超时透传、按 `RetryPolicy.maxAttempts` 重试 |
| 2026-06-10 | B-P3-04 Tool 风险等级：`ToolDefinition` / 管理端 / DB / 运行时结果同步 `riskLevel`，高风险 Tool 可在调用结果中识别 |
| 2026-06-10 | B-P3-05 OpenAI-compatible 抽象：新增 `OpenAiCompatibleChatClient` 并让百炼真实调用复用兼容协议模板 |
| 2026-06-10 | B-P3-09 审计日志：配置变更与运行调用审计，新增 runtime audit 领域、内存/DB 仓储、V10 迁移和定向/集成测试 |
| 2026-06-10 | B-P3-10 开放 API 文档化：新增静态 OpenAPI 3.0 文档、联调说明与文档覆盖校验测试 |
| 2026-06-10 | B-P3-11 SDK 第一版：同步 Java SDK 客户端封装能力执行、Header 透传与错误映射，补 SDK 快速接入文档和单元测试 |
| 2026-06-11 | B-P3-12 流式接口：新增 Runtime SSE 执行入口 `/execute/stream`，输出 STARTED/COMPLETED/FAILED 事件并同步 OpenAPI/API reference |
| 2026-06-12 | B-P3-13 错误码与排障指南：补齐错误码表、Trace 透传规范、排障流程与 SSE FAILED 处理规则 |
| 2026-06-14 | PH2 设计稿：新增 B-PH2-00a Center 统一列表查询、B-PH2-00b Tool debug invoke（见 phase-2-governance-design §4.0） |
| 2026-06-16 | B-PH2-01 用量与额度治理：新增 `usage` 子域额度账户、调用前余额拦截、调用后成本扣减、`GET /api/runtime/usage/account`、V11 `qz_rt_usage_account` 迁移与 OpenAPI/API reference |
| 2026-06-17 | B-PH2-00a Center 统一列表查询：六类 Center 元数据列表统一返回 `CenterPageResult`，支持 `page/size/sort/keyword/status` 与资源专属 filter，并补 OpenAPI/API reference 与集成测试 |
| 2026-06-17 | 进度同步：新增 PH2 当前剩余功能清单；B-PH2-00b 标记为进行中，已完成 RED 集成测试和部分 runtime/tool 调试实现，剩余 controller、文档与测试收口 |
| 2026-06-17 | B-PH2-00b Tool 调试调用：新增 `/api/runtime/tools/{toolCode}/debug-invoke`，支持 Schema 校验、跳过 Skill 绑定的 `ToolRuntime.invokeDebug`、HIGH 风险确认、`TOOL_DEBUG_INVOKE` 审计、OpenAPI/API reference 与集成测试 |
| 2026-06-20 | B-PH2-02 Prompt 发布与灰度：V12 迁移、`PromptReleaseService` + 五类 Admin API、`PromptReleaseRouter` 运行时选版、单测与 OpenAPI/API reference |
| 2026-06-20 | B-PH2-03 Capability 发布与版本控制：V13 迁移、多版本/发布指针/租户启停、`CapabilityVersionResolver` 运行时解析、观测 stats 按 `{code}@{version}` 聚合、OpenAPI/API reference |
| 2026-06-20 | B-PH2-04 模型治理增强：`DefaultModelGovernance` 熔断降级、`GET /api/runtime/models/governance`、LLM 超时取自 `ModelDefinition.timeoutMs`、OpenAPI/API reference 与单测 |
| 2026-06-20 | B-PH2-05 Trace 与审计增强：TraceSpan 步骤树、审计查询 API、`RUNTIME_FAILURE` + errorStack、V14 迁移与集成测试 |
| 2026-06-20 | B-PH2-06 子域包结构：runtime release/router、policy、trace/harness 收敛；center 六域 entity/mapper 下沉（S7 收尾） |
| 2026-06-20 | B-PH3-02 SkillLoader/OutputGovernance 增强：Skill 依赖解析、禁止词检测、日志脱敏增强、包迁移 |
| 2026-06-20 | B-PH3-01（部分）会话与反馈：session/feedback 子域、V15 迁移、会话 API、反馈 API、能力执行写入会话消息 |
| 2026-06-20 | B-PH3-01 知识库与文件：knowledge/file 子域、V16 迁移、检索/文件 API、Prompt 上下文注入、治理参数白名单 |
| 2026-06-20 | B-PH4-01 Tool Adapter/MCP/OTel：ToolAdapterRegistry + Java/HTTP/MCP Adapter、HttpMcpToolClient、TraceSpan OTLP 导出桥 |
| 2026-06-20 | B-PH4-02 PolicyHarness 增强：RBAC/限流/额度/模型熔断预检收口至 PolicyHarness，RuntimeHarness 灰度版本解析 |
| 2026-06-09 | B-PH5-01 多 Agent 与任务治理：planner/coordinator/reflection/budget 子域、DefaultAgentRuntime 接入、治理参数白名单、单测与文档 |
| 2026-06-09 | B-PH5-02 PH5 深化：LlmTaskPlanner、ExecutionReflector 自反思重试、COST_FIRST 模型策略、MultiAgentResultMerger、CompositeTaskPlanner |
| 2026-06-09 | B-PH5-03 计划驱动 Tool 选择：PlanDrivenToolSelector、tool.echo 演示数据、CapabilityRuntimePh5GovernanceControllerTest、前端联调说明 |
| 2026-06-23 | 平台分层 P0 收尾：IT 加固、移除 starter、更新 platform-architecture 与 CMS 测试命令 |
| 2026-06-23 | Phase 3 CMS 收尾：V28 权限/版本/字典；内容版本回滚；C 端 ops；封禁吊销令牌；app-server IT |
| 2026-06-24 | CMS P1 增强：V29~V31；内置角色权限 + 工作台多角色 IT；benefit_def 规范化；PlatformLocalCache；BillingLifecycle/learning invoke IT |
| 2026-06-24 | CMS P2 起步：支付回调 IT、消息模板 render/SPI/预览、组织开户 IT；IT schema 对齐 `org_role` |
| 2026-06-22 | CMS P2-06 Redis L2：`PlatformLocalCache` 双级缓存 + 失效广播；默认 `redis-enabled=false` |
| 2026-06-24 | CMS P2 收口：P2-04~06 工单/公告定向/Redis L2；全量 `./mvnw clean test` 全绿 |
| 2026-06-25 | B-AI-00 模型提供商 M:N：V38 三表、`/api/center/model-providers`、`providerBindings`、OpenAPI 与集成测试 |
| 2026-06-22 | B-AI-01~03：API Key AES 加密、`LlmRouteRegistry` 启动预注册、ReAct 多轮 Tool；smoke 改用 `COG_SMOKE_API_KEY` 写入提供商 |
| 2026-06-29 | B-APP-TUTORING-P3-01~05：学习辅导 P3（画像/错题/计划/练习/治理/invoke 收口）+ V45 迁移 + 集成测试 |
| 2026-06-29 | **B-APP-PHASE-B-01~05：C 端 Phase B（practice/review/today/import/insights BFF）** + V46 迁移 + Gateway `/api/practice/**` + 契约分页适配 + 集成测试 |
| 2026-06-25 | 规划 §十六 B-FILE-*；新增 `frontend-file-integration.md`（对照 ztx3-file 缺口与前端契约） |
