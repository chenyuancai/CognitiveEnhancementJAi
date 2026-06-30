# 文件服务前端对接文档

> **仓库：** `CognitiveEnhancementJAi`（后端） + `CognitiveEnhancementJAiView`（前端）  
> **参考实现：** `ztx3-file`（招标通三代文件微服务）  
> **配对：** [`frontend-integration-guide.md`](./frontend-integration-guide.md)、[`frontend-cms-integration.md`](../../CognitiveEnhancementJAiView/docs/frontend-cms-integration.md)  
> **更新日期：** 2026-06-25

---

## 1. 背景：为什么需要独立文件模块

当前启知（CE）仓库**没有**类似 `ztx3-file` 的通用文件微服务：

| 能力 | ztx3-file | CE 现状 |
|------|-----------|---------|
| Multipart 上传 | ✅ `/upload` | ❌ |
| 流式下载 / 预览 | ✅ `/down`、`/preview` | ❌ |
| OSS 策略（MinIO/磁盘） | ✅ `IFileStrategyService` | ❌ |
| 分片上传 | ✅ `multipart/*` | ❌ |
| 临时文件 + Ensure 确认 | ✅ `FileStatusEnum` + `ensure` | ❌ |
| 元数据表 `ztx_file` | ✅ | ❌（仅有 Runtime 轻量表） |
| Admin 文件管理台 | ✅ | ❌ |
| C 端上传 | ✅ | ❌ |

CE 现有两条**弱相关**能力：

1. **Runtime PH3 文件子域**（`/api/runtime/files`）：仅登记外部 `storagePath` + **Mock 同步解析**，供 AI 执行注入 `[File Context]`。  
2. **CMS CSV 导入**（`/api/admin/content/import-jobs`）：把 CSV **文本**写入任务表异步解析，**不是**二进制对象存储。

产品文档（C 端 P1 导入：PDF/DOCX/图片）与 `F-PH3-02` 文件上传页，均依赖**待建设的平台文件模块**。

---

## 2. 功能清单与 API 状态总览

| ID | 场景 | 建议路由（前端） | API 状态 | 接口前缀 |
|----|------|------------------|----------|----------|
| F-FILE-01 | CMS 素材库 / 附件管理 | `/cms/files` | ⏳ 待后端 | `/api/admin/files`（规划） |
| F-FILE-02 | C 端用户上传（头像/作业） | `/profile/upload` 等 | ⏳ 待后端 | `/api/app/files`（规划） |
| F-FILE-03 | 执行工作台附件 | `/cms/runtime/execute` 表单项 | 🟡 可 Mock 联调 | `/api/runtime/files`（已有骨架） |
| F-FILE-04 | 知识 CSV 批量导入 | `/cms/content/import` | ✅ 已就绪 | `/api/admin/content/import-jobs` |
| F-FILE-05 | 大文件分片上传 | 通用上传组件 | ⏳ 待后端 | `/api/common/files/multipart/*`（规划） |
| F-FILE-06 | 富文本内嵌图片 | 编辑器 | ⏳ 待后端 | `POST .../upload-html`（规划，参考 ztx3） |

图例：**✅** 后端已就绪；**🟡** 骨架/Mock 可联调；**⏳** 需新建 `platform-file` 模块后对接。

---

## 3. 现状可对接：Runtime 文件（F-FILE-03）

> 适用：AI 控制台执行页、Harness 带附件能力。解析结果为 Mock，不含真实 PDF/OCR。

### 3.1 接口一览

基路径：`/api/runtime/files`  
请求头：`Authorization`、`X-Tenant-Code`（缺省 `platform`）、`X-Trace-Id`（可选）

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/runtime/files` | 注册上传元数据（**不上传二进制**） |
| `GET` | `/api/runtime/files/{fileId}` | 查询上传记录 |
| `POST` | `/api/runtime/files/parse` | 启动解析（Body JSON，非 path） |
| `GET` | `/api/runtime/files/{fileId}/parse-result` | 最新解析结果 |

> **注意：** `api-reference.md` 旧版曾写 `POST .../files/{fileId}/parse`，以实现为准：`POST .../files/parse` + Body `{ "fileId": "..." }`。

### 3.2 注册元数据

```http
POST /api/runtime/files
Content-Type: application/json
Authorization: Bearer <token>
X-Tenant-Code: platform

{
  "fileName": "report.pdf",
  "contentType": "application/pdf",
  "sizeBytes": 102400,
  "storagePath": "oss://bucket/path/report.pdf",
  "checksum": "sha256:abc..."
}
```

**响应 `data`（`FileUploadRecord`）：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `fileId` | string | UUID，后续 parse / execute 使用 |
| `fileName` | string | 原始文件名 |
| `contentType` | string | MIME |
| `sizeBytes` | long | 字节数 |
| `storagePath` | string | 外部存储路径（客户端自行上传后填入） |
| `checksum` | string | 可选 |
| `status` | string | `UPLOADED` |
| `recordedAt` | string | ISO-8601 |

### 3.3 解析与轮询

```http
POST /api/runtime/files/parse
{ "fileId": "<fileId>" }
```

**响应 `FileParseTask`：**

| 字段 | 说明 |
|------|------|
| `taskId` | 任务 ID |
| `fileId` | 关联文件 |
| `status` | `PENDING` / `RUNNING` / `SUCCEEDED` / `FAILED` |
| `parseResult` | Mock 时为 `{"textPreview":"mock parsed: ..."}` |
| `errorMessage` | 失败原因 |

当前实现为**同步 Mock**：`startParse` 调用后立即 `SUCCEEDED`，前端仍可按轮询模式写 UI 以兼容未来异步 Worker。

### 3.4 接入能力执行

```http
POST /api/runtime/capabilities/execute
{
  "capabilityCode": "cap.demo",
  "input": { "question": "总结附件" },
  "parameters": {
    "fileId": "<fileId>"
  }
}
```

`DefaultPromptResolver` 在存在 `parameters.fileId` 时注入 `[File Context]`；无解析结果会自动触发 parse。

### 3.5 前端推荐流程（Mock 阶段）

```text
1. （临时）客户端 mock storagePath 或对接自建 OSS 直传
2. POST /api/runtime/files          → fileId
3. POST /api/runtime/files/parse    → SUCCEEDED
4. GET  /api/runtime/files/{id}/parse-result  → 确认
5. POST /api/runtime/capabilities/execute     → parameters.fileId
```

---

## 4. 现状可对接：CMS CSV 导入（F-FILE-04）

> 适用：管理端「导入中心」。仅 **CSV 文本**，非通用文件存储。

| 方法 | 路径 | 权限 | Body |
|------|------|------|------|
| `POST` | `/api/admin/content/import-jobs/page` | `admin:content:update` | `{ "current", "size" }` |
| `GET` | `/api/admin/content/import-jobs/{id}` | `admin:content:update` | — |
| `POST` | `/api/admin/content/import-jobs` | `admin:content:create` | 见下 |

**创建任务：**

```json
{
  "fileName": "contents.csv",
  "fileContent": "title,type,body\n启知二课,ARTICLE,正文..."
}
```

| 字段 | 说明 |
|------|------|
| `fileName` | 展示用文件名 |
| `fileContent` | CSV 明文或 Base64 |
| `fileUrl` | 字段存在但**当前未拉取 URL**，请勿依赖 |
| `sourceContentId` | 可选，关联源内容 |

**任务状态：** `PENDING` → `RUNNING` → `SUCCEEDED` / `PARTIAL` / `FAILED`；`resultJson` 含失败行明细。  
前端轮询 `GET .../{id}` 直至终态（Worker 默认约 30s 周期）。

---

## 5. 规划：平台文件模块（参考 ztx3-file）

> 以下接口为**设计草案**，供前端预研组件；后端任务 ID 见 [`backend-task-checklist.md`](./backend-task-checklist.md) §十六 `B-FILE-*`。

### 5.1 模块形态（对齐 ztx3-file）

```text
cognitive-enhancement-ai-file-api      # DTO + Feign 契约（可选）
cognitive-enhancement-ai-file          # 或合入 platform/file 包
  ├── web/          Admin + App + 公共上传 Controller
  ├── service/      上传/下载/预览编排
  ├── spi/          IFileStorageStrategy（MinIO / S3 / 本地盘）
  ├── domain/       FileRecord, FileClient
  └── repository/   qz_file 元数据
```

参考 ztx3 核心模式：

- DB 存元数据（`ztx_file` 对标 `qz_file`），二进制在 OSS。  
- 上传默认 **未确认（UNCONFIRMED）**，业务落库后调 **ensure** 防孤儿文件。  
- 定时任务清理未确认 / 过期文件。  
- 多租户：`tenant_id` + `X-Tenant-Code`。

### 5.2 规划 API（管理端）

基路径：`/api/admin/files`

| 方法 | 路径 | 说明 | 参考 ztx3 |
|------|------|------|-----------|
| `POST` | `/page` | 分页检索 | Inner list |
| `GET` | `/{fileId}` | 元数据详情 | `info/{fileId}` |
| `POST` | `/upload` | Multipart 上传 | `POST /upload` |
| `GET` | `/{fileId}/download` | 代理下载流 | `GET /down` |
| `GET` | `/{fileId}/preview` | 预览流（图片/PDF） | `GET /preview` |
| `POST` | `/ensure` | 确认文件生效 | `POST /ensure` |
| `DELETE` | `/{fileId}` | 逻辑删除 | `removeFile` |

### 5.3 规划 API（C 端）

基路径：`/api/app/files`

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/upload` | 用户上传（限额/类型校验） |
| `GET` | `/{fileId}` | 自己的文件元数据 |
| `GET` | `/{fileId}/download` | 下载（鉴权归属） |

### 5.4 规划 API（公共 / 大文件）

基路径：`/api/common/files`（或网关 `/api/v1/common/file`，与 ztx3 对齐可选）

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/multipart/create` | 分片任务创建 |
| `PUT` | `/multipart/chunks/upload` | 上传分片 |
| `POST` | `/multipart/complete` | 合并完成 |
| `POST` | `/upload-html` | 富文本转存 |

### 5.5 规划与 Runtime 的衔接

```text
C/App 上传 → platform-file 返回 fileId + storagePath + link
          → POST /api/runtime/files（登记，或直接复用 platform fileId）
          → POST /api/runtime/files/parse（真实解析 Worker：PDF/DOCX/OCR）
          → execute parameters.fileId
```

### 5.6 ztx3 → CE 能力映射

| ztx3-file | CE 规划 | 首期是否做 |
|-----------|---------|------------|
| Multipart upload | `POST .../upload` | ✅ P0 |
| download / preview | GET 流式 | ✅ P0 |
| ensure + 清理 Job | ensure + XXL/ShedLock | ✅ P0 |
| MinIO / Disk 策略 | `IFileStorageStrategy` | ✅ P0 |
| 分片上传 | multipart/* | P1 |
| 压缩 ZIP | compress | P2 |
| OpenAPI 网关 | 复用 CE Gateway | 可选 |
| CA 签章上传 | — | 不做 |
| DomainEnum 路径 | `bizDomain` 字段可选 | P2 |

---

## 6. 前端组件建议

### 6.1 `@ce/shared` 上传组件（待 file 模块落地）

| 组件 | 职责 |
|------|------|
| `FileUpload` | 小文件 Multipart → `fileId` |
| `ChunkUpload` | 分片 + 断点续传 |
| `FilePreview` | 图片/PDF 预览 URL 或 blob |
| `ensureFiles(ids)` | 业务保存成功后批量确认 |

### 6.2 与现有页面对接

| 页面 | 任务 ID | 对接方式 |
|------|---------|----------|
| 导入中心 | F-FILE-04 / F-CMS-04 | 已接 CSV `fileContent` |
| 执行工作台 | F-FILE-03 / F-AI-07 | 先 Runtime 元数据 + mock parse |
| C 端学习/导入 | F-PH3-02 | 等 `B-FILE-02` App 上传 |
| 内容正文富文本 | F-CMS-03 增强 | 等 `upload-html` 或图片 upload |
| CMS 素材库 | F-FILE-01 | 等 Admin 文件分页 |

---

## 7. 配置与限制（规划）

| 配置键 | 说明 | 参考 |
|--------|------|------|
| `cog.file.storage` | `minio` / `local` | `ztx.oss.name` |
| `cog.file.max-size` | 单文件上限 | multipart 500MB |
| `cog.file.allowed-types` | MIME 白名单 | 安全配置 |
| `cog.file.temp-expire-hours` | 未确认过期 | `expire_time` |

---

## 8. 验收清单

### 现状（可立即测）

- [ ] Runtime：注册 → parse → parse-result → execute 带 `fileId`
- [ ] CMS：CSV 导入成功 / 部分失败行展示

### 文件模块建成后

- [ ] Admin Multipart 上传 → 列表可见 → 下载/预览
- [ ] 业务保存后 ensure，未确认文件被定时清理
- [ ] C 端上传仅本人可下载
- [ ] 大文件分片上传进度条
- [ ] Runtime parse 读取真实 OSS 对象（非 Mock）

---

## 9. 变更记录

| 日期 | 说明 |
|------|------|
| 2026-06-25 | 初版：对照 ztx3-file 梳理缺口；Runtime/CSV 现状接口；规划 B-FILE 契约 |
