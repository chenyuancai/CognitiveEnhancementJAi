# 前端任务清单

## 一、基础设施（先做）

| 序号 | 任务 | 说明 |
|:---:|------|------|
| 1 | 初始化前端项目 | Vue 3 / React / 其他，选一个 |
| 2 | 安装依赖 | 路由、HTTP 客户端、UI 组件库 |
| 3 | 封装 HTTP 请求工具 | 统一添加 `Authorization: Bearer <token>` 请求头 |
| 4 | 响应拦截处理 | 401 跳转登录页，其他错误统一 Toast 提示 |
| 5 | 路由守卫 | 未登录拦截，已登录免登 |

---

## 二、认证模块

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 6 | 登录页 | `POST /api/users/login`（用户名+密码） |
| 7 | 注册页 | `POST /api/users/register`（用户名+密码+昵称+邮箱+手机号） |
| 8 | 登录状态管理 | localStorage 存 token + 用户信息 |
| 9 | 登出功能 | 清除 token，跳转登录页 |

---

## 三、布局框架

| 序号 | 任务 | 说明 |
|:---:|------|------|
| 10 | 侧边栏导航 | 6 个元数据菜单 + 用户管理 |
| 11 | 顶部栏 | 显示当前用户名 + 登出按钮 |
| 12 | 主内容区路由 | 路由切换，面包屑可选 |

---

## 四、元数据管理后台（6 套相同结构的 CRUD 页面）

每套页面 = **列表页** + **新增/编辑弹窗** + 暂无删除接口（后端目前只有 GET/POST/PUT）

### 4.1 模型管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 13 | 模型列表页 | `GET /api/center/models` |
| 14 | 模型详情查看 | `GET /api/center/models/{code}` |
| 15 | 新增模型弹窗 | `POST /api/center/models` |
| 16 | 编辑模型弹窗 | `PUT /api/center/models/{code}` |

表单字段：`providerCode`, `providerName`, `modelCode`, `modelName`, `modelType`, `endpoint`, `apiKey`, `timeoutMs`, `retryTimes`, `status`, `routePriority`, `fallbackModelCode`

### 4.2 提示词模板管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 17 | 提示词列表页 | `GET /api/center/prompts` |
| 18 | 提示词详情查看 | `GET /api/center/prompts/{code}` |
| 19 | 新增提示词弹窗 | `POST /api/center/prompts` |
| 20 | 编辑提示词弹窗 | `PUT /api/center/prompts/{code}` |

表单字段：`promptCode`, `promptName`, `scenarioCode`, `version`, `templateContent`, `variableSchema`, `outputSchema`, `status`, `publishedAt`

### 4.3 工具管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 21 | 工具列表页 | `GET /api/center/tools` |
| 22 | 工具详情查看 | `GET /api/center/tools/{code}` |
| 23 | 新增工具弹窗 | `POST /api/center/tools` |
| 24 | 编辑工具弹窗 | `PUT /api/center/tools/{code}` |

表单字段：`toolCode`, `toolName`, `protocolType`, `requestSchema`, `responseSchema`, `permissionScope`, `timeoutMs`, `retryPolicy`, `implRef`, `status`

### 4.4 技能管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 25 | 技能列表页 | `GET /api/center/skills` |
| 26 | 技能详情查看 | `GET /api/center/skills/{code}` |
| 27 | 新增技能弹窗 | `POST /api/center/skills` |
| 28 | 编辑技能弹窗 | `PUT /api/center/skills/{code}` |

表单字段：`skillCode`, `skillName`, `skillType`, `skillInstruction`, `boundToolCodes`, `riskLevel`, `forbiddenRules`, `examples`, `status`

### 4.5 Agent 管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 29 | Agent 列表页 | `GET /api/center/agents` |
| 30 | Agent 详情查看 | `GET /api/center/agents/{code}` |
| 31 | 新增 Agent 弹窗 | `POST /api/center/agents` |
| 32 | 编辑 Agent 弹窗 | `PUT /api/center/agents/{code}` |

表单字段：`agentCode`, `agentName`, `roleDesc`, `goalDesc`, `modelCode`, `maxSteps`, `maxCost`, `timeoutMs`, `allowedSkillCodes`, `parameterConstraints`, `status`

### 4.6 能力管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 33 | 能力列表页 | `GET /api/center/capabilities` |
| 34 | 能力详情查看 | `GET /api/center/capabilities/{code}` |
| 35 | 新增能力弹窗 | `POST /api/center/capabilities` |
| 36 | 编辑能力弹窗 | `PUT /api/center/capabilities/{code}` |

表单字段：`capabilityCode`, `capabilityName`, `capabilityDesc`, `inputSchema`, `outputSchema`, `parameterConstraints`, `executeMode`, `boundAgentCode`, `riskLevel`, `needHumanConfirm`, `status`

---

## 五、用户管理

| 序号 | 任务 | 接口 |
|:---:|------|------|
| 37 | 用户列表页 | `GET /api/users?page=&size=` |
| 38 | 用户详情查看 | `GET /api/users/{id}` |

---

## 五、PH5 能力试跑联调面板

> 完整接口说明见 Knife4j（经网关）：`http://localhost:8801/doc.html`（分组：Runtime - 能力执行）

能力试跑页调用 `POST /api/runtime/capabilities/execute`，在 `parameters` 中透传 PH5 治理开关：

| 控件 | parameters 字段 | 建议默认值 |
|------|-----------------|------------|
| 任务规划 | `planningEnabled` | false |
| 规划模式 | `planningMode`（`RULE` / `LLM`） | `RULE` |
| 计划驱动 Tool | `planDrivenToolEnabled` | false |
| 指定 Tool | `preferredToolCode` | 空（下拉：技能绑定 Tool） |
| 自反思 | `reflectionEnabled` | false |
| 多 Agent | `multiAgentEnabled` | false |
| 子 Agent | `delegateAgentCodes` | 空 |
| 执行策略 | `executionStrategy` | `BALANCED` |
| 任务预算 | `taskBudgetAmount` | 空 |

结果面板建议展示：`taskPlan`、`selectedToolCode`、`reflectionApplied`、`budgetRemaining`、`multiAgentResults`、`mergedBusinessOutput`。

---

## 六、可选增强

| 序号 | 任务 | 说明 |
|:---:|------|------|
| 39 | 表单校验 | 前端必填、格式、范围校验 |
| 40 | 列表筛选/排序 | 按状态、名称等筛选 |
| 41 | 操作反馈 | 保存成功/失败的 Toast 提示 |
| 42 | 加载状态 | 表格和表单的 loading 状态 |

---

## 推荐启动顺序

1. 1~5（基础设施）
2. 6~9（认证模块）
3. 10~12（布局框架）
4. 13~16（模型管理，先打通一个完整的 CRUD 验证端到端）
5. 17~36（其余 5 个元数据页）
6. 37~38（用户管理）
7. 39~42（可选增强）
