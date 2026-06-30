# Phase B 字段差异清单（M3）

> **对照来源：** `CognitiveEnhancementJAiView/docs/cog-api-contracts/`（`auth.md`、`billing.md`、`ops.md` 等）  
> **更新日期：** 2026-06-29  
> **用途：** 前端关 Mock 联调前核对仍缺失或命名不一致的字段；已对齐项可忽略。

---

## 1. 已对齐（M0~M2）

| 域 | 端点 | 说明 |
|----|------|------|
| 鉴权 | `GET /api/app/auth/me` | `accountId`、`userId`、`displayName`、`email`、`quota.total/used/remaining/remainingLabel` |
| 练习 | `/api/practice/**` | 会话、choice/essay、essay/stream、debrief、insight |
| 复习 | `/api/app/review/**` | 分页 `items/page/size/total`（兼容 `records/current`） |
| 今日 | `GET /api/app/today` | welcome、statCards、reviewSection、importStatus 等 BFF 聚合 |
| 导入 | `/api/app/import-tasks/**` | CRUD + SSE `progress/done/failed` |
| 画像 BFF | `GET /api/app/insights/overview` | 聚合练习 + 辅导画像 + 复习 |
| 辅导 SSE | `POST /api/app/tutoring/chat/stream` | 契约帧 `delta/done/error`（引擎内部事件经 `AppTutoringSseAdapter`） |
| 学习模式 | `GET /api/app/learning/modes` | `recommendedPath` 指向 tutoring 主路径 |

---

## 2. Billing（已补齐展示字段）

| 契约字段 | 实现位置 |
|----------|----------|
| `packages[].badge` / `highlight` | `AppBillingVoAssembler` + `AppBillingLabelSupport` |
| `orders[].payChannelLabel` | `AppBillingVoAssembler.toOrderVo` |
| `quota.warningThreshold` | `AppMeVoAssembler.toQuota` |
| `subscription.renewAtLabel` | `AppMeVoAssembler.toMembership`（`membership.renewAtLabel`） |

---

## 3. Ops（已补齐展示字段）

| 契约字段 | 实现位置 |
|----------|----------|
| `banners[].actionType` / `actionUrl` | `AppOpsService.toBannerVo` + `AppOpsLabelSupport` |
| `announcements[].priority` | `AppOpsService.toAnnouncementVo` |
| `inAppMessages[].categoryLabel` | `AppInAppMessageService.toVo` |
| `supportTickets[].statusLabel` | `AppSupportTicketService.toVo` |

---

## 4. Insights BFF（已知简化）

| 契约字段 | 当前实现 | 备注 |
|----------|----------|------|
| `heatmap.cells[].intensity` | 规则生成占位 | 首期无真实学习日历埋点 |
| `accuracyTrendHighlights` | 基于近 7 次练习推导 | 非 mock 全量 30 天 |
| `tagMastery[].trend` | 静态 `flat` | 待接入历史快照 |

---

## 5. 网关与路径

| 项 | 状态 |
|----|------|
| `/api/practice/**` → 8804 | ✅ `app-practice-service` 路由（须在 `/api/**` 兜底之前） |
| `/api/app/**` → 8804 | ✅ 已有 |

---

## 6. 验证命令

```bash
# Phase B 定向集成测试
./mvnw -pl cognitive-enhancement-ai-app-server -am install -DskipTests -q
./mvnw -pl cognitive-enhancement-ai-app-server test \
  -Dtest='AppAuthMeContractTest,AppPractice*IntegrationTest,AppReviewIntegrationTest,AppTodayIntegrationTest,AppImportTask*IntegrationTest,AppInsightsIntegrationTest,AppTutoring*IntegrationTest,AppWebLayerArchitectureTest'

# 网关练习路由
./mvnw -pl cognitive-enhancement-ai-gateway test \
  -Dtest='AppPracticeGatewayRouteIntegrationTest'
```
