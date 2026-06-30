# 前端对接指南（CMS 平台期）

> **配对文档：**  
> - 前端任务清单：[`CognitiveEnhancementJAiView/docs/frontend-task-checklist.md`](../../CognitiveEnhancementJAiView/docs/frontend-task-checklist.md)  
> - CMS 对接详表：[`CognitiveEnhancementJAiView/docs/frontend-cms-integration.md`](../../CognitiveEnhancementJAiView/docs/frontend-cms-integration.md)  
> - **文件专章：** [`docs/frontend-file-integration.md`](./frontend-file-integration.md)（对照 ztx3-file）  
> - OpenAPI 契约：[`docs/openapi.yaml`](./openapi.yaml)  
> - 联调补充：[`docs/api-reference.md`](./api-reference.md)  
> **更新日期：** 2026-06-29

本文档按**功能清单 → 接口路径 → 请求/响应要点**组织，供 `@ce/admin`（管理端）与 `@ce/portal`（C 端）联调使用。

---

## 1. 联调环境

| 项 | 值 |
|----|-----|
| 网关（推荐） | `http://localhost:8801` |
| Admin 直连 | `http://localhost:8803`（`apps/web` 代理 `/api` 时常用） |
| 在线文档 | `http://localhost:8801/doc.html` |
| OpenAPI | `GET /v3/api-docs` |

| 路径前缀 | 受众 | 说明 |
|----------|------|------|
| `/api/admin/**` | 管理后台 | IAM、内容、计费、运营、工作台 |
| `/api/app/**` | C 端 | 注册、知识库、计费、运营 |
| `/api/practice/**` | C 端 | 练习会话、作答、AI 评分流（路由至 8804） |
| `/api/runtime/**` | 能力运行时 | 已有 `@ce/admin` Runtime 模块 |
| `/api/center/**` | 元数据 | 已有 Center 六套 CRUD |
| `/oauth2/**` | 认证 | 登录、刷新令牌 |

---

## 2. 鉴权与请求头

| 请求头 | 必填 | 说明 |
|--------|:----:|------|
| `Authorization` | 是* | `Bearer <access_token>` |
| `X-Tenant-Code` | 否 | 缺省 `platform` |
| `X-Trace-Id` | 否 | 排障用，响应会回传 |

\* 公开接口：`POST /api/app/auth/register`、`/oauth2/token` 等标注 `@SecurityRequirements` 的接口。

**拦截器建议：** `401`/`A0401` → 清 token 跳 `/login`；`403`/`A0403` → 权限提示。

---

## 3. 统一响应

```json
{
  "success": true,
  "code": null,
  "message": null,
  "data": {},
  "traceId": "..."
}
```

**分页约定：** Admin/App 业务接口多用 `PageResult`（`records`、`total`、`current`、`size`、`pages`）；Center 元数据用 `CenterPageResult`（`items`、`total`、`page`、`size`、`totalPages`、`hasNext`）。

---

## 3.1 功能总览清单（按 CMS 菜单）

> 路由以 `packages/cms` 为准；`API` 列：`✅` 后端已就绪，`🟡` 前端本地 Mock，`⏳` 待后端。

| ID | 菜单模块 | 页面路由 | 权限点 | API | 接口前缀 |
|----|----------|----------|--------|-----|----------|
| F-CMS-01 | 工作台 | `/cms/workbench` | `workbench:view` | ✅ | `/api/admin/workbench` |
| — | 运营看板 | `/cms/operation/dashboard` | — | ✅ | `/api/admin/operations/dashboard` |
| F-CMS-02 | 用户管理 | `/cms/users` | `admin:user:view` | ✅ | `/api/admin/users` |
| F-CMS-02 | 角色权限 | `/cms/account/roles` | `admin:role:view` | ✅ | `/api/admin/roles` |
| F-CMS-02 | 权限管理 | `/cms/account/permissions` | `admin:permission:view` | ✅ | `/api/admin/permissions` |
| F-CMS-02 | 账号安全 | `/cms/account/security` | `admin:security:view` | ✅ | `/api/admin/system/security-configs` |
| F-CMS-06 | 会员等级 | `/cms/member/levels` | `admin:member:view` | ✅ | `/api/admin/membership/levels` |
| F-CMS-06 | 会员审计 | `/cms/member/records` | `admin:member:view` | ✅ | `/api/admin/membership/levels/change-logs` |
| F-CMS-05 | 订单管理 | `/cms/billing/orders` | `admin:order:view` | ✅ | `/api/admin/billing/orders` |
| F-CMS-05 | 订阅套餐 | `/cms/billing/packages` | `admin:package:view` | ✅ | `/api/admin/billing/subscription-packages` |
| F-CMS-05 | 订阅管理 | `/cms/billing/subscriptions` | `admin:subscription:view` | ✅ | `/api/admin/billing/subscriptions` |
| F-CMS-05 | 额度包 | `/cms/billing/quota-packages` | `admin:quota-package:view` | ✅ | `/api/admin/billing/quota-packages` |
| F-CMS-07 | 额度账户 | `/cms/billing/quota-accounts` | `admin:quota-account:view` | ✅ | `/api/admin/quota/accounts` |
| F-CMS-07 | 资金流水 | `/cms/billing/financial-records` | `admin:financial:view` | ✅ | `/api/admin/billing/financial-records` |
| F-CMS-07 | Token 流水 | `/cms/billing/token-records` | `admin:token-record:view` | ✅ | `/api/admin/quota/token-records` |
| F-CMS-03 | 内容条目 | `/cms/content/items` | `admin:content:view` | ✅ | `/api/admin/content/contents` |
| F-CMS-04 | 标签治理 | `/cms/content/tags` | `admin:tag:view` | ✅ | `/api/admin/content/tags` |
| F-CMS-04 | 知识包 | `/cms/content/knowledge-packages` | `admin:knowledge-package:view` | ✅ | `/api/admin/content/knowledge-packages` |
| F-CMS-04 | 导入中心 | `/cms/content/import` | `admin:content-import:view` | ✅ | `/api/admin/content/import-jobs` |
| F-CMS-03 | 内容审核 | `/cms/content/review` | `admin:content-review:view` | ✅ | `.../contents/audit` |
| F-CMS-11 | AI 仪表盘 | `/cms/ai/dashboard` | `admin:ai:dashboard:view` | 🟡 | 聚合多接口 |
| F-CMS-11 | 成本看板 | `/cms/ai/cost` | `admin:ai:cost:view` | ✅ | `/api/admin/ai/cost-dashboard` |
| F-CMS-11 | 路由策略 | `/cms/ai/routing` | `admin:ai:routing:view` | ✅ | `/api/admin/ai/routing-overview` |
| **F-AI-00** | **提供商管理** | `/cms/center/providers` | `admin:ai:provider:view` | **🟡** | **⏳ 见 §8.2** |
| **F-AI-01** | **模型管理** | `/cms/center/models` | `admin:ai:model:view` | ✅ | `/api/center/models` |
| F-AI-02 | Agent | `/cms/center/agents` | `admin:ai:agent:view` | ✅ | `/api/center/agents` |
| F-AI-03 | 能力 | `/cms/center/capabilities` | `admin:ai:capability:view` | ✅ | `/api/center/capabilities` |
| F-AI-04 | 提示词 | `/cms/center/prompts` | `admin:ai:prompt:view` | ✅ | `/api/center/prompts` |
| F-AI-05 | 技能 | `/cms/center/skills` | `admin:ai:skill:view` | ✅ | `/api/center/skills` |
| F-AI-06 | 工具 | `/cms/center/tools` | `admin:ai:tool:view` | ✅ | `/api/center/tools` |
| F-AI-07 | 执行工作台 | `/cms/runtime/execute` | `admin:ai:execute:view` | ✅ | `/api/runtime/capabilities` |
| F-AI-08 | 执行/用量/检测 | `/cms/runtime/*` | 各 `admin:ai:*` | ✅ | `/api/runtime/observations` |
| F-AI-09 | Harness | `/cms/runtime/harness` | `admin:ai:harness:view` | ✅ | `/api/admin/harness` |
| F-CMS-08 | 公告 Banner | `/cms/operation/announcements` | `admin:announcement:view` | ✅ | `/api/admin/operations/banners` 等 |
| F-CMS-08 | 消息模板 | `/cms/operation/message-templates` | `admin:message-template:view` | ✅ | `/api/admin/operations/message-templates` |
| F-CMS-09 | 组织治理 | （待挂菜单） | `admin:user:view` | ✅ | `/api/admin/orgs` |
| F-CMS-10 | 字典/枚举 | `/cms/system/dict` | — | ✅ | `/api/base/dict/**`、`/api/base/enum/**`（base-server 8805） |
| F-CMS-10 | 系统设置 | `/cms/system/settings` | `admin:system:view` | ✅ | `/api/admin/system/features` 等 |
| F-CMS-10 | Feature 开关 | `/cms/system/feature-switches` | `admin:feature:view` | ✅ | `/api/admin/system/features` |
| F-CMS-P01~06 | C 端 Portal | `/`（`@ce/portal`） | App JWT | ✅ | `/api/app/**` |
| **F-FILE-03** | **执行工作台附件** | `/cms/runtime/execute` | `admin:ai:execute:view` | **🟡** | `/api/runtime/files`（Mock 解析） |
| **F-FILE-04** | **CSV 导入** | `/cms/content/import` | `admin:content-import:view` | **✅** | `/api/admin/content/import-jobs` |
| **F-FILE-01~02** | **通用文件服务** | `/cms/files`、C 端上传 | — | **⏳** | 规划 `/api/admin/files`、`/api/app/files`；见 [`frontend-file-integration.md`](./frontend-file-integration.md) |

---

## 4. HTTP 约定（CMS 管理端 / C 端）

| 场景 | 方法 | 路径模式 | Body |
|------|------|----------|------|
| 条件分页 | `POST` | `.../page` | 查询 DTO（含 `current`/`size`/筛选） |
| 条件聚合 | `POST` | `.../query` | 查询 DTO |
| 新建 | `POST` | 资源基路径 | 保存 DTO |
| 更新 | `POST` | `.../update` | 保存 DTO（含 `id`） |
| 状态/动作 | `POST` | `.../status`、`.../audit` 等 | 命令 DTO（含 `id`） |
| 详情 | `GET` | `.../{id}` | — |

写操作**不在 URL 放资源 id**（详情 GET 除外），标识放 Body。

---

## 5. 状态枚举（JSON 字符串）

| 枚举 | 取值 | 用途 |
|------|------|------|
| `OrderStatus` | `PENDING` / `PAID` / `FULFILLED` / `REFUNDED` / `CLOSED` | 订单 |
| `ContentStatus` | `DRAFT` / `PENDING` / `PUBLISHED` / `REJECTED` / `OFFLINE` | 内容审核 |
| `SupportTicketStatus` | `OPEN` / `IN_PROGRESS` / `RESOLVED` / `CLOSED` | 工单 |
| `IamUserStatus` | `ENABLED` / `DISABLED` / `BANNED` | 用户 |
| `EnableStatus` | `ENABLED` / `DISABLED` | 租户、套餐、等级等 |

错误码见 [`api-reference.md`](./api-reference.md)（`A0400`~`A0429`、`B0500`、`C0500`）。

---

## 5.1 联调种子数据（知识内容域）

Flyway **V37** 为租户 `platform` 写入默认各 1 条，前端首屏可直接用固定 ID 联调：

| 实体 | ID | 关键字段 |
|------|----|----------|
| 内容 | `1` | `启知入门指南`，`PUBLISHED`，`minLevelCode=FREE` |
| 标签 | `1` | `入门`，`#409EFF`；已绑定内容 `1` |
| 知识包 | `1` | `默认知识包`，`ENABLED` |
| 知识包条目 | `1` | `packageId=1`，`contentId=1` |

详细验证命令与 C 端路径见前端 [`frontend-cms-integration.md`](../../CognitiveEnhancementJAiView/docs/frontend-cms-integration.md) 第 3 节。

---

## 6. 管理端功能清单与接口（F-CMS-01 ~ 12）

### F-CMS-01 角色化工作台

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/admin/workbench` | 角色化首页（待办+指标+快捷入口） |
| `POST` | `/api/admin/workbench/dashboard/query` | 看板聚合；Body: `{ from?, to?, refresh? }` |
| `GET` | `/api/admin/workbench/overview` | 概览卡片 |
| `POST` | `/api/admin/workbench/trends/query` | 趋势曲线 |
| `GET` | `/api/admin/workbench/todo` | 待办/告警 |

权限：`workbench:view`。

---

### F-CMS-02 IAM（用户 / 租户 / 角色 / 权限）

| 功能 | 方法 | 路径 | Body 要点 |
|------|------|------|-----------|
| 当前管理员 | `GET` | `/api/admin/auth/me` | — |
| 用户分页 | `POST` | `/api/admin/users/page` | `current`,`size`,`keyword?` |
| 用户详情 | `GET` | `/api/admin/users/{id}` | — |
| 用户状态 | `POST` | `/api/admin/users/status` | `{ id, status, banReason? }` |
| 租户分页 | `POST` | `/api/admin/iam/tenants/page` | 分页字段 |
| 租户 CRUD | `POST` | `/api/admin/iam/tenants`、`/update`、`/status` | 保存/状态 DTO |
| 角色分页 | `POST` | `/api/admin/roles/page` | — |
| 角色 CRUD | `POST` | `/api/admin/roles`、`/update` | — |
| 角色权限 | `POST` | `/api/admin/roles/permissions` | `{ id, permissionIds }` |
| 权限树 | `GET` | `/api/admin/permissions/tree` | — |
| 权限分页 | `POST` | `/api/admin/permissions/page` | — |

---

### F-CMS-03 内容管理

| 功能 | 方法 | 路径 | Body 要点 |
|------|------|------|-----------|
| 分页 | `POST` | `/api/admin/content/contents/page` | `status?`,`keyword?` |
| 详情 | `GET` | `/api/admin/content/contents/{id}` | — |
| 新建/更新 | `POST` | `/api/admin/content/contents`、`/update` | 标题、正文、类型等 |
| 审核 | `POST` | `/api/admin/content/contents/audit` | `{ id, approved, remark? }` |
| 下线 | `POST` | `/api/admin/content/contents/offline` | `{ id }` |
| 版本列表 | `GET` | `/api/admin/content/contents/{id}/versions` | — |
| 回滚 | `POST` | `/api/admin/content/contents/rollback` | `{ id, versionNo }` |
| 标签绑定 | `GET`/`POST` | `.../tags` | `tagIds` |

---

### F-CMS-04 标签 / 知识包 / CSV 导入

| 模块 | 方法 | 路径 |
|------|------|------|
| 标签分页 | `POST` | `/api/admin/content/tags/page` |
| 标签保存 | `POST` | `/api/admin/content/tags`、`/update` |
| 知识包分页 | `POST` | `/api/admin/content/knowledge-packages/page` |
| 知识包条目 | `GET` | `.../knowledge-packages/{id}/items` |
| 条目维护 | `POST` | `.../items` |
| 导入任务 | `POST` | `/api/admin/content/import-jobs/page`、`POST` 创建 |
| 创建导入 | `POST` | `/api/admin/content/import-jobs` | `{ fileContent, sourceContentId? }` CSV |

---

### F-CMS-05 计费（套餐 / 订单 / 订阅记录）

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 订阅套餐分页 | `POST` | `/api/admin/billing/subscription-packages/page` | |
| 套餐维护 | `POST` | `.../subscription-packages`、`/update` | |
| 额度包 | `POST` | `.../quota-packages/page`、CRUD | |
| 订单分页 | `POST` | `/api/admin/billing/orders/page` | |
| 订单详情 | `GET` | `/api/admin/billing/orders/{id}` | |
| 标记支付 | `POST` | `/api/admin/billing/orders/mark-paid` | `{ orderId, ... }` |
| 取消 | `POST` | `/api/admin/billing/orders/cancel` | `{ id }` |
| 退款 | `POST` | `/api/admin/billing/orders/refund` | `{ orderId, amountFen, reason? }` |
| **订阅记录** | `POST` | `/api/admin/billing/subscriptions/page` | 含注册赠送 FREE 订阅 |
| 资金流水 | `POST` | `/api/admin/billing/financial-records/page` | |

**注册赠送 FREE 订阅：** 用户注册后自动写入 `qz_bill_subscription`（套餐 `sub.free.default`，`order_id=null`，`level_code=FREE`）。管理端在「订阅记录」列表可查到。

---

### F-CMS-06 会员等级

| 功能 | 方法 | 路径 |
|------|------|------|
| 等级分页 | `POST` | `/api/admin/membership/levels/page` |
| 下拉全量 | `GET` | `/api/admin/membership/levels/all?segment=2C` |
| 手动授予 | `POST` | `/api/admin/membership/levels/grant` | `{ accountId, levelId, expireAt?, remark? }`；授予 FREE 时同步写订阅记录 |
| 会员列表 | `POST` | `/api/admin/membership/members/page` |
| 调级 | `POST` | `/api/admin/membership/members/level` | `{ id, levelId }` |
| 变更日志 | `POST` | `/api/admin/membership/levels/change-logs/page` |

---

### F-CMS-07 额度

| 功能 | 方法 | 路径 |
|------|------|------|
| 账户额度 | `GET` | `/api/admin/quota/accounts/{accountId}` |
| 调整额度 | `POST` | `/api/admin/quota/accounts/adjust` |
| Token 流水 | `POST` | `/api/admin/quota/token-records/page` |
| 成员分配列表 | `GET` | `/api/admin/quota/accounts/{accountId}/member-allocs` |
| 设置成员额度 | `POST` | 同上基路径 | Body: 分配 DTO |
| 移除分配 | `DELETE` | `.../member-allocs/{userId}` |

---

### F-CMS-08 运营

| 模块 | 分页 | 详情 GET | 保存 POST | 其他 |
|------|------|----------|-----------|------|
| Banner | `.../operations/banners/page` | `/{id}` | `/`、`/update` | |
| 公告 | `.../announcements/page` | `/{id}` | `/`、`/update` | 支持定向字段 |
| 消息模板 | `.../message-templates/page` | `/{id}` | `/`、`/update` | `POST .../preview`、`POST .../send` |
| 工单 | `.../support-tickets/page` | `/{id}` | `/`、`/update` | `POST .../status` |
| 运营看板 | — | — | `POST .../operations/dashboard/query` | `{ preset? }` |

---

### F-CMS-09 组织治理（2B/2G）

| 功能 | 方法 | 路径 |
|------|------|------|
| 组织分页 | `POST` | `/api/admin/orgs/page` |
| 开通组织 | `POST` | `/api/admin/orgs` | `CreateOrganizationRequest` |
| 组织详情 | `GET` | `/api/admin/orgs/{orgId}` |
| 部门列表 | `GET` | `/api/admin/orgs/{orgId}/departments` |
| 部门维护 | `POST` | `.../departments`、`/departments/update` |
| 成员列表 | `GET` | `/api/admin/orgs/{orgId}/members` |
| 添加成员 | `POST` | `.../members` |
| 移除成员 | `DELETE` | `/api/admin/orgs/{orgId}/members/{memberId}` |

---

### F-CMS-10 字典/枚举（base-server）

经网关 `8801` 转发至 base-server（`8805`），路径前缀 `/api/base/**`。

| 模块 | 方法 | 路径 |
|------|------|------|
| 字典类型分页 | `POST` | `/api/base/dict/types/page` |
| 字典类型保存 | `POST` | `/api/base/dict/types/save` |
| 字典项列表 | `POST` | `/api/base/dict/items/list` |
| 字典项保存 | `POST` | `/api/base/dict/items/save` |
| 公共读取 | `GET` | `/api/base/dict/{code}/items` |
| 枚举类型 | `POST` | `/api/base/enum/types/page`、`/save`、`/tree` |
| 枚举项 | `POST` | `/api/base/enum/items/list`、`/save` |

### F-CMS-10 系统设置（admin-server）

| 模块 | 方法 | 路径 |
|------|------|------|
| 功能开关 | `POST` | `/api/admin/system/features/page`、`/update` |
| 安全配置 | `POST` | `/api/admin/system/security-configs/page`、`/update` |
| 审计日志 | `POST` | `/api/admin/system/audit-logs/page` |
| 健康检查 | `GET` | `/api/admin/system/health` |
| 刷新健康 | `POST` | `/api/admin/system/health/refresh` |

---

### F-CMS-11 AI 看板（只读）

| 方法 | 路径 | Body |
|------|------|------|
| `POST` | `/api/admin/ai/cost-dashboard/query` | `{ preset?: TODAY \| LAST_7_DAYS \| LAST_30_DAYS }` |
| `GET` | `/api/admin/ai/routing-overview` | — |

---

## 7. C 端功能清单与接口（F-CMS-P01 ~ P06）

### F-CMS-P01 注册与登录

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 注册 | `POST` | `/api/app/auth/register` | `UserRegisterRequest`；成功后开户：FREE 会员 + 月额度 + **FREE 订阅记录** |
| 登录 | `POST` | `/oauth2/token` | 标准 OAuth2 密码模式 |
| 当前用户 | `GET` | `/api/app/auth/me` | 用户、账户、会员、额度摘要 |

**`GET /api/app/auth/me` 响应字段（节选）：**

```json
{
  "user": { "id", "username", "nickname", "status" },
  "account": { "id", "accountType", "segment", "displayName" },
  "membership": { "levelCode": "FREE", "levelName", "expireAt" },
  "quota": { "cycleRemaining", "giftRemaining", "topupRemaining" }
}
```

---

### F-CMS-P02 知识库

| 功能 | 方法 | 路径 |
|------|------|------|
| 内容分页 | `POST` | `/api/app/knowledge/contents/page` |
| 内容详情 | `GET` | `/api/app/knowledge/contents/{id}` | 含 `locked`（等级不足） |
| 知识包列表 | `GET` | `/api/app/knowledge/packages` |
| 知识包树 | `GET` | `/api/app/knowledge/packages/{id}/tree` |

---

### F-CMS-P03 学习与会员

| 功能 | 方法 | 路径 |
|------|------|------|
| 学习模式 | `GET` | `/api/app/learning/modes` |
| 学习调用 | `POST` | `/api/app/learning/invoke` | QA 等模式 |
| 会员摘要 | `GET` | `/api/app/membership/me` | 可选 |
| 额度摘要 | `GET` | `/api/app/quota/me` | |
| 额度流水 | `POST` | `/api/app/quota/token-records/me/page` | |
| 成员额度（组织） | `GET` | `/api/app/quota/member-alloc/me` | |

---

### F-CMS-P04 计费与支付

| 功能 | 方法 | 路径 | Body 要点 |
|------|------|------|-----------|
| 在售订阅套餐 | `POST` | `/api/app/billing/subscription-packages/page` | `{ segment? }`；**不含** `sub.free.default`（OFF_SALE） |
| 在售额度包 | `POST` | `/api/app/billing/quota-packages/page` | |
| 创建订单 | `POST` | `/api/app/billing/orders` | `CreateOrderRequest` |
| 我的订单 | `POST` | `/api/app/billing/orders/page` | `{ current, size, status? }` |
| 订单详情 | `GET` | `/api/app/billing/orders/{id}` | |
| **发起支付** | `POST` | `/api/app/billing/orders/pay` | `{ orderId, payChannel?: MOCK\|WECHAT\|ALIPAY }` |

**支付响应 `AppPayOrderResultVO`：** `orderId`、`payChannel`、`clientParams`（调起微信/支付宝/MOCK）、`status=PENDING`。

支付回调（渠道服务器）：`POST /api/app/billing/pay-callback`（Body 见 `PaymentCallbackRequest`）。

---

### F-CMS-P05 运营触达

| 功能 | 方法 | 路径 |
|------|------|------|
| Banner | `POST` | `/api/app/ops/banners/page` |
| 公告 | `GET` | `/api/app/ops/announcements` | 服务端按会员等级/用户定向过滤 |
| 站内信分页 | `POST` | `/api/app/ops/in-app-messages/page` |
| 标记已读 | `POST` | `/api/app/ops/in-app-messages/read` | `{ id }` |
| 工单分页 | `POST` | `/api/app/ops/support-tickets/page` |
| 工单详情 | `GET` | `/api/app/ops/support-tickets/{id}` |
| 提交工单 | `POST` | `/api/app/ops/support-tickets` |

---

### F-CMS-P07 AI 学习辅导助手

| 功能 | 方法 | 路径 |
|------|------|------|
| 学习辅导对话 | `POST` | `/api/app/tutoring/chat` |
| 学习辅导对话（同步 SSE） | `POST` | `/api/app/tutoring/chat/stream` |
| 学习辅导对话（异步 + SSE） | `POST` | `/api/app/tutoring/chat/async` |
| 学习画像 | `GET` | `/api/app/tutoring/profile` |
| 错题分页 | `POST` | `/api/app/tutoring/mistakes/page` |
| 活跃学习计划 | `GET` | `/api/app/tutoring/plan/active` |
| 待完成练习 | `GET` | `/api/app/tutoring/practice/pending/{sessionId}` |
| 建立 SSE 连接 | `GET` | `/api/sse/connect?sessionId=可选` |
| 断开 SSE 连接 | `POST` | `/api/sse/disconnect?sessionId=可选` |
| 会话消息历史 | `GET` | `/api/app/tutoring/sessions/{sessionId}/messages` |
| 会话分页列表 | `POST` | `/api/app/tutoring/sessions/page` |

**请求示例（`POST /api/app/tutoring/chat`）：**

```json
{
  "sessionId": "可选，空则新建",
  "message": "我不理解函数单调性",
  "capabilityCode": "capability.chat.tutoring",
  "references": {
    "messageIds": [],
    "fileIds": [],
    "knowledgeIds": ["1"],
    "selectedText": "可选选中文本"
  },
  "options": { "stream": false }
}
```

**响应要点：** `answer`、`intent`、`strategy`、`blueprint`、`state.needUserReply`、`state.nextExpectedAction`、`state.stuckCount`、`state.masteryLevel`。

**SSE 阶段事件：** `PROFILE_LOADED` → `CONTEXT_LOADED` → `STRATEGY_SELECTED` → `BLUEPRINT_READY` →（可选）`GOVERNANCE_APPLIED` → `COMPLETED`。异步模式先 `GET /api/sse/connect`（**必须带 `Authorization: Bearer {token}`**，原生 `EventSource` 不支持自定义头时请用 `@microsoft/fetch-event-source` 等库），再 `POST /api/app/tutoring/chat/async`。`sessionId` 建连参数须与异步对话请求保持一致（未带 `sessionId` 时走用户级通道 `userId`）。

**兼容说明：** `POST /api/app/learning/invoke` 的 `TUTORING` 模式已委托至 `/api/app/tutoring/chat`。

**门禁：** 需 `ai.tutoring` 会员权益与足够 Token 额度；引用 `knowledgeIds` 时校验内容发布态与 `minLevelCode`。

**SSE 契约帧（`/chat/stream`）：** 对外输出 `delta`（阶段进度文本）/ `done`（完成载荷）/ `error`；引擎内部事件名经适配器转换，前端勿依赖 `PROFILE_LOADED` 等原始枚举。

---

### F-CMS-P08 Phase B：练习 / 复习 / 今日 / 导入 / 画像

| 功能 | 方法 | 路径 |
|------|------|------|
| 创建练习会话 | `POST` | `/api/practice/sessions` |
| 提交选择题 | `POST` | `/api/practice/sessions/{id}/answers/choice` |
| 提交问答题 | `POST` | `/api/practice/sessions/{id}/answers/essay` |
| 问答题评分流 | `GET` | `/api/practice/sessions/{id}/answers/essay/stream?answerId=` |
| 练习复盘 | `GET` | `/api/practice/sessions/{id}/debrief` |
| 练习洞察 | `GET` | `/api/practice/insight` |
| 待复习分页 | `POST` | `/api/app/review/pending/page` |
| 错题本分页 | `POST` | `/api/app/review/error-book/page` |
| 最近练习分页 | `POST` | `/api/app/review/recent-sessions/page` |
| 今日聚合 | `GET` | `/api/app/today` |
| 创建导入任务 | `POST` | `/api/app/import-tasks` |
| 导入任务分页 | `POST` | `/api/app/import-tasks/page` |
| 导入任务详情 | `GET` | `/api/app/import-tasks/{id}` |
| 重试导入 | `POST` | `/api/app/import-tasks/{id}/retry` |
| 导入进度 SSE | `GET` | `/api/app/import-tasks/{id}/progress/stream` |
| 学习画像概览 BFF | `GET` | `/api/app/insights/overview` |

**导入任务创建（完整版工作流）：**

1. `POST /api/app/files/upload`（`multipart/form-data`，字段 `file`）→ 取 `data.id` 作为 `fileId`
2. `POST /api/app/import-tasks`，请求体示例：

```json
{
  "importBizType": "KNOWLEDGE_DOCUMENT",
  "channel": "file",
  "fileId": 10001,
  "title": "民法典讲义",
  "fileName": "civil-law.md",
  "targetType": "knowledge",
  "tags": ["民法"],
  "aiEnhanced": true,
  "autoQuiz": false
}
```

| `importBizType` | 说明 |
|-----------------|------|
| `KNOWLEDGE_DOCUMENT` | 知识文档（默认：分块 + 向量 + AI 摘要） |
| `KNOWLEDGE_URL` | URL 导入（同上） |
| `COURSE_HANDOUT` | 课程讲义 |
| `EXAM_PAPER` | 试卷（偏测验，无向量） |
| `MISTAKE_ARCHIVE` | 错题归档 |
| `PRACTICE_SOURCE` | 练习素材 |

**SSE 阶段名：** `parsing` → `normalizing` → `indexing` → `enriching` → `done`；完成帧含 `libraryItemId`（即 `qz_kb_content.id`）。

**Admin 同步调试（不落任务表）：** `POST /api/admin/import-workflow/debug/sync`（需 `admin:content:update`）

**Harness 场景模板：** `GET /api/admin/harness/scenario-templates` 含「知识文件导入」；`inputParams.workflowType=IMPORT_KB_FILE_PARSE` 且需提供 `fileId`。

**分页约定（C 端 Phase B）：** 请求体 `page`/`size`（兼容 `current`）；响应 `items`、`page`、`size`、`total`（内部 `PageResult` 经 `AppPageVO` 映射）。

**字段差异：** 见 [`docs/phase-b-field-gap.md`](./phase-b-field-gap.md)。

---

## 8. AI 控制台：模型提供商 → 模型 → 元数据链

### 8.1 域模型与页面依赖

```text
模型提供商 (Provider)     绑定表 (Binding)           模型 (Model)
  providerCode ◄──────► providerCode + modelCode ◄── modelCode
  defaultEndpoint            endpoint（可覆盖默认）      modelType
  apiKey                     apiKey（可覆盖）           timeoutMs / retryTimes
  providerType               routePriority / status
                                                      Agent / Capability
                                                         modelCode 绑定
```

**产品规则：**

1. **先维护提供商**，再创建模型；模型与提供商为 **多对多**，通过 `providerBindings` 维护。
2. 每条绑定可覆盖 `endpoint`、`apiKey`、`routePriority`、`status`；未填时继承提供商默认。
3. `ModelResult.providerCode` 等字段为 **首选路由镜像**（`routePriority` 最高且 `ENABLED` 的绑定），兼容旧前端。
4. 模型列表支持按 `providerCode` 筛选；仪表盘统计提供商数量应调用提供商接口。

**前端现状：**

| 页面 | 包路径 | 数据层 |
|------|--------|--------|
| 提供商管理 | `packages/cms/src/views/ai-provider-management` | `provider.api.js` → 后端 HTTP |
| 模型管理 | `packages/cms/src/views/ai-model-management` | `model.api.js` → 后端 HTTP |

---

### 8.2 F-AI-00 模型提供商管理（✅ 后端已就绪）

基路径：`/api/center/model-providers`

| 功能 | 方法 | 路径 | Body / 说明 |
|------|------|------|-------------|
| 分页列表 | `POST` | `/page` | `{ page, size, sort?, keyword?, status?, providerType? }` |
| 全量下拉 | `GET` | `/all` | 仅 `ENABLED`；供模型表单 `el-select` |
| 详情 | `GET` | `/{providerCode}` | — |
| 创建 | `POST` | `/` | `ModelProviderUpsertRequest` |
| 更新 | `POST` | `/update` | Body 含 `providerCode`（不可改） |

**`ModelProviderUpsertRequest` / `ModelProviderResult` 字段：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:----:|------|
| `providerCode` | string | 创建时 | 唯一，创建后不可改 |
| `providerName` | string | ✓ | 展示名 |
| `providerType` | string | ✓ | `OPENAI_COMPATIBLE` / `DASHSCOPE` / `ANTHROPIC` 等 |
| `defaultEndpoint` | string | | 默认 Chat Completions URL |
| `apiKey` | string | 创建时 ✓ | API Key 明文提交；服务端 AES-GCM 加密入库；响应仅 `apiKeyConfigured` + `apiKeyMask` |
| `description` | string | | 备注 |
| `status` | string | ✓ | `ENABLED` / `DISABLED` |

**分页响应：** `CenterPageResult<ModelProviderResult>`。

**加密配置（运维）：** 生产环境必须设置 `COG_CRYPTO_MASTER_KEY`（与开发默认不同）。配置项 `cog.crypto.api-key-encryption-enabled` 默认 `true`。

**前端对接：** `provider.api.js` 已切换 HTTP；表单使用密码框提交 `apiKey`；`provider.storage.js` 仅作离线演示 fallback。

---

### 8.3 F-AI-01 模型管理（✅ 后端已就绪）

基路径：`/api/center/models`

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页列表 | `POST` | `/page` | Body: `ModelPageQuery` |
| 详情 | `GET` | `/{modelCode}` | — |
| 创建 | `POST` | `/` | Body: `ModelUpsertRequest` |
| 更新 | `POST` | `/update` | Body 含 `modelCode` |

**`ModelPageQuery` 筛选字段：**

```json
{
  "page": 1,
  "size": 20,
  "sort": "routePriority,asc",
  "keyword": "gpt",
  "status": "ENABLED",
  "providerCode": "dashscope",
  "modelType": "CHAT"
}
```

**`ModelUpsertRequest` / `ModelResult` 字段：**

| 字段 | 说明 |
|------|------|
| `providerBindings` | **推荐**；`ModelProviderBindingRequest[]`，维护模型与提供商多对多 |
| `providerCode` | 兼容单绑定写法；仅传此项时自动转为单条 `providerBindings` |
| `providerName` | 冗余展示；建议与提供商表一致 |
| `modelCode` | 唯一；创建后不可改 |
| `modelName` | 展示名 |
| `modelType` | 如 `CHAT` / `EMBEDDING` |
| `endpoint` | 单绑定兼容字段；多绑定时写入对应 binding |
| `apiKey` | 绑定级 Key 覆盖；未填则继承提供商默认 Key |
| `timeoutMs` | 超时毫秒 |
| `retryTimes` | 重试次数 |
| `status` | 模型主状态 `ENABLED` / `DISABLED` |
| `routePriority` | 单绑定兼容；多绑定时写入 binding |
| `fallbackModelCode` | 降级目标 `modelCode` |
| `providers` | 响应字段；全部绑定列表 `ModelProviderBindingResult[]` |

**创建模型联调示例（多对多）：**

```http
POST /api/center/models
Authorization: Bearer <token>
X-Tenant-Code: platform

{
  "modelCode": "qwen-plus",
  "modelName": "通义千问 Plus",
  "modelType": "CHAT",
  "timeoutMs": 60000,
  "retryTimes": 1,
  "status": "ENABLED",
  "fallbackModelCode": "",
  "providerBindings": [
    {
      "providerCode": "dashscope",
      "routePriority": 10,
      "status": "ENABLED"
    },
    {
      "providerCode": "openai",
      "endpoint": "https://api.openai.com/v1/chat/completions",
      "apiKey": "sk-xxx",
      "routePriority": 20,
      "status": "ENABLED"
    }
  ]
}
```

---

### 8.4 F-AI-02 ~ F-AI-06 Center 其余元数据（✅）

六套资源共用 Center 约定：`POST /page`、`GET /{code}`、`POST /`、`POST /update`。

| ID | 资源 | 基路径 | 专属筛选 |
|----|------|--------|----------|
| F-AI-02 | Agent | `/api/center/agents` | `modelCode` |
| F-AI-03 | Capability | `/api/center/capabilities` | `boundAgentCode`, `riskLevel`, `executeMode` |
| F-AI-04 | Prompt | `/api/center/prompts` | `scenarioCode` |
| F-AI-05 | Skill | `/api/center/skills` | `skillType`, `riskLevel` |
| F-AI-06 | Tool | `/api/center/tools` | `protocolType`, `riskLevel` |

**Prompt / Capability 生命周期（灰度）：**

| 动作 | Prompt | Capability |
|------|--------|------------|
| 版本列表 | `GET /{code}/versions` | 同左 |
| 新建草稿 | `POST /{code}/drafts` | 同左 |
| 发布 | `POST /{code}/publish` | 同左 |
| 下线 | `POST /{code}/offline` | — |
| 灰度 | `PUT /{code}/gray` | `PUT /{code}/gray` |
| 租户启停 | — | `PUT /{code}/tenants/{tenantCode}` `{ "enabled": false }` |

---

### 8.5 F-AI-07 ~ F-AI-09 Runtime 调试与 Harness（✅）

#### 模型连通性与治理

| 功能 | 方法 | 路径 |
|------|------|------|
| 单次连通检查 | `POST` | `/api/runtime/models/check` |
| 批量刷新状态 | `POST` | `/api/runtime/models/statuses/refresh` |
| 状态列表 | `GET` | `/api/runtime/models/statuses?providerCode=&modelCode=` |
| 健康概览 | `GET` | `/api/runtime/models/overview` |
| 治理状态 | `GET` | `/api/runtime/models/governance` |

#### 能力执行与观测

| 功能 | 方法 | 路径 |
|------|------|------|
| 同步执行 | `POST` | `/api/runtime/capabilities/execute` |
| 流式执行 | `POST` | `/api/runtime/capabilities/execute/stream` |
| Tool 调试 | `POST` | `/api/runtime/tools/{toolCode}/debug-invoke` |
| 执行记录 | `GET` | `/api/runtime/observations/executions` |
| 执行详情 | `GET` | `/api/runtime/observations/executions/{traceId}` |
| 聚合统计 | `GET` | `/api/runtime/observations/stats` |

**ReAct 多轮 Tool 对话（绑定 Tool 的 Agent 默认启用）：**

| 参数 | 位置 | 说明 |
|------|------|------|
| `reactEnabled` | `parameters` | `false` 时退回单轮 `TOOL_THEN_LLM` |
| `reactMaxIterations` | `parameters` | 最大 ReAct 轮次，默认 5 |

全局配置：`cog.runtime.react.enabled`、`cog.runtime.react.max-iterations`。启动时 `LlmRouteRegistry` 预注册全部 `modelCode@providerCode` 路由，Admin 变更模型/提供商后自动刷新。

#### Harness

| 功能 | 方法 | 路径 |
|------|------|------|
| 运行 | `POST` | `/api/admin/harness/run` |
| 报告列表 | `GET` | `/api/admin/harness/reports` |
| 进度推送 | WS | `/ws/harness` |

---

## 9. 典型联调流程

### 9.1 注册 → FREE 订阅可查

```
POST /api/app/auth/register
→ GET /api/app/auth/me（membership.levelCode=FREE, quota.cycleRemaining>0）
→ Admin: POST /api/admin/billing/subscriptions/page（accountId=该账户）
  应看到 levelCode=FREE、packageCode=sub.free.default、orderId 为空
```

### 9.2 C 端付费订阅

```
POST /api/app/billing/subscription-packages/page
→ POST /api/app/billing/orders（packageId=专业版）
→ POST /api/app/billing/orders/pay { orderId, payChannel: "MOCK" }
→ 联调环境 POST pay-callback（或 Mock 网关自动回调）
→ GET /api/app/billing/orders/{id} → PAID/FULFILLED
→ GET /api/app/auth/me → 会员/额度变化
```

### 9.3 内容发布到 C 端可见

```
Admin 创建草稿 → 更新 → 审核通过 PUBLISHED
→ C 端 POST /api/app/knowledge/contents/page 可见
```

### 9.4 提供商 → 模型（推荐顺序）

```
POST /api/center/model-providers（维护厂商）
→ GET /api/center/model-providers/all（模型表单下拉）
→ POST /api/center/models（providerBindings 多对多）
→ POST /api/runtime/models/check 验证连通性
```

### 9.5 Runtime 文件 → 能力执行（Mock 阶段，F-FILE-03）

```
POST /api/runtime/files（登记 storagePath 等元数据）→ fileId
→ POST /api/runtime/files/parse { fileId }
→ GET /api/runtime/files/{fileId}/parse-result → SUCCEEDED
→ POST /api/runtime/capabilities/execute { parameters: { fileId } }
```

详见 [`frontend-file-integration.md`](./frontend-file-integration.md) §3。

---

## 10. 文件服务（F-FILE-*）

> **专章文档：** [`frontend-file-integration.md`](./frontend-file-integration.md)（对照 `ztx3-file` 缺口 + 现状 + 规划 API）  
> **结论：** CE 尚无通用 OSS 文件模块；**可立即对接** Runtime 元数据/Mock 解析与 CMS CSV 导入；Multipart 上传/下载/预览待 `B-FILE-*` 建设。

| ID | 状态 | 说明 |
|----|------|------|
| F-FILE-03 | 🟡 | `/api/runtime/files` 四套接口 + execute 带 `fileId` |
| F-FILE-04 | ✅ | `/api/admin/content/import-jobs` CSV 异步导入 |
| F-FILE-01/02/05/06 | ⏳ | 参考 ztx3：`upload`、`down`、`preview`、`ensure`、分片 |

---

## 11. 本地验证

```bash
# 全量
./mvnw test

# CMS 集成（节选）
./mvnw -pl cognitive-enhancement-ai-admin-server test \
  '-Dtest=Admin*IntegrationTest,OpenApiDocumentTest' \
  -Dsurefire.failIfNoSpecifiedTests=false

./mvnw -pl cognitive-enhancement-ai-app-server test \
  '-Dtest=App*IntegrationTest' \
  -Dsurefire.failIfNoSpecifiedTests=false

# 免费订阅单测
./mvnw -pl cognitive-enhancement-ai-platform test \
  -Dtest=FreeSubscriptionServiceTest,MembershipOnboardingServiceTest
```

---

## 12. 变更记录

| 日期 | 说明 |
|------|------|
| 2026-06-25 | 新增 §10 文件服务专章；链至 `frontend-file-integration.md`（ztx3-file 对照） |
| 2026-06-25 | 新增 §3.1 功能总览表；§8 AI 控制台专章（提供商→模型域模型、F-AI-00 契约、Center/Runtime 接口明细） |
| 2026-06-25 | 补充 V37 知识域默认种子说明；详见前端 frontend-cms-integration.md §3 |
| 2026-06-25 | 扩写 CMS 功能清单与逐接口对照；补充注册 FREE 订阅记录说明 |
| 2026-06-25 | 统一 POST `/page`、`/update`、命令 Body 带 `id` 约定 |
| 2026-06-24 | 初版 CMS 联调说明 |
