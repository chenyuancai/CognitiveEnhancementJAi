# 启知 CMS 生产部署指南

本文说明启知管理后台（CMS）三进程架构的生产部署顺序、关键配置与验收方式。

## 架构概览

| 进程 | 模块 | 默认端口 | 职责 |
|------|------|----------|------|
| Gateway | `cognitive-enhancement-ai-gateway` | **8801** | 对外入口、路由、可选 Bearer 验签 |
| Auth | `cognitive-enhancement-ai-auth` | **8802** | OAuth2 授权服务器（password grant、JWK） |
| Admin-Server | `cognitive-enhancement-ai-admin-server` | **8803** | 管理后台 API（Admin / Runtime / Center） |
| App-Server | `cognitive-enhancement-ai-app-server` | **8804** | C 端 API（`/api/app/**`） |

对外统一访问 Gateway：`http://<host>:8801`。OpenAPI 文档：`http://<host>:8801/doc.html`。

```
客户端 ──► Gateway:8801 ──┬──► Auth:8802        (/oauth2/**)
                          ├──► Admin-Server:8803 (/api/admin/**, /api/center/**, …)
                          └──► App-Server:8804    (/api/app/**)
```

## 前置条件

- **JDK 21**
- **MySQL 8**（库名默认 `cog`，账号见各模块 `application.yml`）
- **Redis**（可选，仅 Gateway 限流需要）
- Maven Wrapper：`./mvnw`

## 1. 数据库与迁移

1. 创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS cog DEFAULT CHARACTER SET utf8mb4;
```

2. 首次启动 **Admin-Server**（带 `prod` profile）时 Flyway 自动执行迁移（含 V25 统一 `qz_` 表前缀），包含 Admin 域表与 OAuth2 JDBC 表（V24）。

3. 确认迁移完成后再启动 Auth（Auth 依赖 OAuth2 客户端/授权表）。

## 2. 构建

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./mvnw package -DskipTests
```

产物：

- `cognitive-enhancement-ai-gateway/target/cognitive-enhancement-ai-gateway-*.jar`
- `cognitive-enhancement-ai-auth/target/cognitive-enhancement-ai-auth-*.jar`
- `cognitive-enhancement-ai-admin-server/target/cognitive-enhancement-ai-admin-server-*.jar`
- `cognitive-enhancement-ai-app-server/target/cognitive-enhancement-ai-app-server-*.jar`

## 3. Auth 服务（8802）

### 启动

```bash
java -jar cognitive-enhancement-ai-auth/target/cognitive-enhancement-ai-auth-*.jar \
  --spring.profiles.active=prod
```

### 关键配置（`application-prod.yml` + 环境变量）

| 配置项 | 生产建议 |
|--------|----------|
| `cog.auth.storage.mode` | `jdbc`（OAuth2 客户端/令牌持久化） |
| `cog.auth.jwk.key-path` | RSA JWK 文件路径，默认 `data/cog-auth/rsa-jwk.json` |
| `cog.auth.issuer` | 对外 Issuer，如 `https://auth.example.com` |
| `spring.datasource.*` | 指向生产 MySQL |

首次启动会自动注册内置客户端 **`cms-client`** / **`cms-secret`**（见 `OAuth2PlatformClientRegistrar`）。

### 默认管理员

种子用户（Flyway/seed）：用户名 `admin`，密码 **`user1234`**（BCrypt）。生产环境请尽快修改。

## 4. Admin-Server 服务（8803）

### 启动

```bash
java -jar cognitive-enhancement-ai-admin-server/target/cognitive-enhancement-ai-admin-server-*.jar \
  --spring.profiles.active=prod
```

### 关键配置（`application-prod.yml`）

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `cog.admin.dev-auth-bypass` | **`false`** | 禁止开发占位身份，必须真实鉴权 |
| `cog.admin.trust-gateway-headers` | 见下方方案 | 是否信任 Gateway 透传的 `X-User-*` |
| `cog.runtime.usage.account.enabled` | **`true`** | Runtime 扣费走 Admin 额度账户 |
| `cog.seed.enabled` | **`false`** | 生产勿重复写入演示种子 |
| `cog.admin.oauth2-jwk-set-uri` | Auth JWK 地址 | 如 `http://127.0.0.1:8802/oauth2/jwks` |

## 5. Gateway 服务（8801）

### 启动

```bash
java -jar cognitive-enhancement-ai-gateway/target/cognitive-enhancement-ai-gateway-*.jar \
  --spring.profiles.active=prod
```

路由规则见 `application.yml`：`/oauth2/**` → Auth，`/api/**` → Starter。

### 鉴权方案（二选一）

**方案 B — 网关集中验签（生产 `application-prod.yml` 默认）**

- `cog.gateway.api-auth.enabled=true`
- Starter 同时设置 `cog.admin.trust-gateway-headers=true`、`cog.jwt.trust-gateway-headers=true`
- Gateway 验签后注入 `X-User-Id`、`X-User-Name`、`X-Tenant-Id`、`X-Roles`、`X-Authorities`

**方案 A — 网关纯转发（开发/调试可选）**

- Gateway `cog.gateway.api-auth.enabled=false`
- Starter 自行校验 `Authorization: Bearer`

生产部署默认采用 **方案 B**。

## 6. 推荐启动顺序

```
MySQL 就绪
    → Starter (Flyway 迁移 + 业务 API)
    → Auth (OAuth2 + JWK)
    → Gateway (对外入口)
```

Auth 与 Starter 可并行，但 **Flyway 须先于 Auth JDBC 客户端注册** 完成。

## 7. 验收

### OAuth2 全链路脚本

三服务均启动后：

```bash
./scripts/e2e-oauth2-login.sh
```

脚本流程：Auth password grant → Gateway `/api/admin/auth/me` → 无 Token 返回 401。

环境变量：

| 变量 | 默认 |
|------|------|
| `AUTH_BASE` | `http://127.0.0.1:8802` |
| `GATEWAY_BASE` | `http://127.0.0.1:8801` |
| `USERNAME` / `PASSWORD` | `admin` / `user1234` |

### 运营看板 API

```bash
curl -s "http://127.0.0.1:8801/api/admin/operations/dashboard?preset=LAST_7_DAYS" \
  -H "Authorization: Bearer <access_token>"
```

需权限点 `admin:user:view`。

### 健康检查

- Gateway / Starter / Auth 均暴露 Spring Boot Actuator（按各模块安全配置开放）

## 8. 安全清单

- [ ] `cog.admin.dev-auth-bypass=false`
- [ ] 修改默认 MySQL 密码与 `cms-client` 密钥
- [ ] JWK 私钥文件权限仅服务账户可读
- [ ] Gateway 对外 HTTPS 终止（Nginx / LB）
- [ ] 生产关闭 `cog.seed.enabled`
- [ ] 限制 Auth `/oauth2/token` 来源 IP 或走内网

## 9. 常见问题

| 现象 | 排查 |
|------|------|
| `/oauth2/token` 401 invalid_client | 检查 Basic 认证 `cms-client:cms-secret`；确认 V24 迁移与 `OAuth2PlatformClientRegistrar` 已执行 |
| Gateway `/api/admin/**` 401 | Bearer 是否过期；方案 A 时 Starter `oauth2-jwk-set-uri` 是否可达 Auth |
| Runtime 未扣额度 | `cog.runtime.usage.account.enabled=true` 且 Admin 额度表有账户记录 |
| Flyway 失败 | 库 `cog` 是否存在；勿手动改已执行 migration 版本 |

## 相关文档

- [BUILD.md](../BUILD.md) — JDK / Maven 基线
- [backend-task-checklist.md](./backend-task-checklist.md) — 后端任务清单
- [scripts/e2e-oauth2-login.sh](../scripts/e2e-oauth2-login.sh) — OAuth2 联调脚本
