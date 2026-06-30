# 后端分包规范

本文档约定各 Maven 模块内的包结构，对齐阿里巴巴 Java 开发手册的分层思想，并按**业务域纵向切分**。

## 模块职责

| 模块 | 职责 |
|------|------|
| `cognitive-enhancement-ai-api` | 对外 API 契约、统一响应 |
| `cognitive-enhancement-ai-core` | 纯领域模型与仓储接口（无 DB/Mapper） |
| `cognitive-enhancement-ai-center` | 元数据管理（Agent/Capability/Tool 等） |
| `cognitive-enhancement-ai-runtime` | 运行时执行、观测、Harness |
| `cognitive-enhancement-ai-platform` | 共享业务域（account/iam/org/membership/quota/billing/content 等） |
| `cognitive-enhancement-ai-admin` | 管理端 Web + RBAC/审计/看板 |
| `cognitive-enhancement-ai-app` | C 端 Web |
| `cognitive-enhancement-ai-admin-server` | Admin 启动与集成（8803） |
| `cognitive-enhancement-ai-app-server` | C 端启动与集成（8804） |

## 业务域包结构（范本：`harness` / `observation`）

每个业务域在模块内拥有独立子包，域内依赖方向**单向**：

```
{module}.{domain}
├── web/          # Controller，仅暴露 DTO/VO
├── service/      # 业务编排，不直接访问 Mapper
├── repository/   # 仓储实现（Persistent* / InMemory* / Db*）
├── spi/          # 域内 SPI（Repository 接口、Recorder 等）
├── entity/       # 表对象（DO），禁止穿透到 Controller
├── mapper/       # MyBatis Mapper，1 表 1 接口，仅 Repository 使用
├── domain/       # 域内业务对象（BO），与 Entity 转换在 Repository
└── dto/          # 接口入参/出参、查询对象
```

### 依赖规则

```
web → service → repository → mapper → entity
         ↓
       spi（接口）
       domain / dto
```

- **Service** 只依赖 `spi` 中的 Repository 接口，**禁止**注入 Mapper。
- **Repository 实现** 负责 Entity ↔ Domain 转换。
- **Controller** 返回 `dto`，不返回 `entity`。
- **core** 不放 Entity、Mapper、Repository 实现。

## 命名约定

| 类型 | 后缀 | 示例 |
|------|------|------|
| 表对象 | `Entity`（后续可统一为 `DO`） | `ExecutionRecordEntity` |
| 域内对象 | 无或业务名 | `ExecutionRecord`、`HarnessReport` |
| 接口传输 | `Request` / `Result` / `Dto` | `HarnessRunRequest` |
| Mapper | `Mapper` | `ExecutionRecordMapper` |
| DB 仓储 | `Persistent*` 或 `Db*` | `PersistentExecutionRecordRepository` |
| 内存仓储 | `InMemory*` | `InMemoryExecutionRecordRepository` |

同一域内持久化实现命名二选一，**不要混用**；center 元数据域当前使用 `Db*`，runtime 观测域使用 `Persistent*`。

## runtime 模块域划分

```
cn.cyc.ai.cog.runtime
├── harness/       # Agent Harness 演练（已对齐）
├── observation/   # 执行记录、用量、观测 API
├── trace/         # TraceSpan 记录与查询（span/、repository/、service/、harness/、otel/）
├── audit/         # 审计日志记录与查询
├── usage/         # 用量额度账户与扣减
├── release/       # 发布路由（router/：Capability/Prompt 版本解析）
├── policy/        # 策略 Harness（DefaultPolicyHarness）与输出治理（output/）
├── skill/         # Skill 装载（loader/DefaultSkillLoader）
├── session/       # 会话与短期上下文（PH3）
├── feedback/      # 执行反馈闭环（PH3）
├── knowledge/     # 知识片段、场景绑定与检索（PH3）
├── file/          # 文件上传元数据与解析任务（PH3）
├── model/         # 模型治理（governance/：熔断、降级）
├── tool/          # Tool 执行（HTTP / MCP / Local / adapter/ / 校验）
├── planner/       # 任务规划（RuleBased / Llm / CompositeTaskPlanner）
├── coordinator/   # 多 Agent 委派、策略与 MultiAgentResultMerger
├── reflection/    # ExecutionLoopGuard + ExecutionReflector 自反思
├── budget/        # 任务级预算控制（PH5：TaskBudgetController）
├── support/       # Runtime 治理参数读取等跨域工具
├── service/       # 跨域运行时（CapabilityRuntime、Llm 等），逐步瘦身
├── spi/           # 跨域 SPI（LlmGateway、AgentRuntime 等）
├── domain/        # 跨域领域对象（逐步迁入各业务域）
├── api/           # 跨域 DTO（逐步迁入各业务域 dto/）
├── config/
└── security/
```

## center 模块（目标结构，分 PR 迁移）

每个元数据域自带持久化子包：

```
cn.cyc.ai.cog.center.{agent|capability|tool|...}
├── web/ 或 *Controller.java
├── service/
├── repository/    # Db* / InMemory*
├── entity/        # 表对象（DO）
└── mapper/        # MyBatis Mapper，仅 Repository 使用
```

`center/entity`、`center/mapper` 顶层平铺为**遗留结构**，已迁移完毕；新代码禁止再放入顶层。

## 迁移顺序

1. ✅ `observation` — repository / service / web / dto / spi / domain
2. ✅ `tool` — http / mcp / validation / local / spi
3. ✅ `trace` / `audit` / `usage` / `release/router` / `policy` / `model/governance` — PH2 S4/S5 最小迁移
4. ✅ `center` 各域 entity + mapper 下沉 — PH2 S7（agent/capability/model/prompt/skill/tool）
5. ⬜ `runtime/service` 剩余类按域继续拆分

每次迁移：**仅搬家 + 改包名 + 改 import**，行为不变；迁移后 `./mvnw test` 全绿再合并。
