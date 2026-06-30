# 构建说明

## 基线要求

- JDK 21
- Maven 使用仓库自带 Wrapper：`./mvnw`

当前父工程在 [pom.xml](/Users/soliloquy/workplace/code/learn/ai/CognitiveEnhancementJAi/pom.xml) 中统一约束为：

- `java.version=21`
- Spring Boot BOM `3.5.6`

保留这组基线的原因：

- Task 1 的目标是完成多模块骨架，不额外引入版本切换变量
- JDK 21 已在父工程显式声明，后续模块共享同一编译基线
- Spring Boot 依赖只通过 BOM 管理，Boot 插件与启动依赖由 `admin-server` / `app-server` 模块承担，库模块不再过早绑定 Boot parent

## 预热本地仓库

默认本地仓库固定在：

```plain
./.mvn/local-repo
```

当前仓库不会提交这个目录，且 `.gitignore` 已忽略它。

如果当前机器已经有可用的 Maven 本地仓库，推荐先复制一份到仓库内再做离线验证：

```bash
./scripts/prewarm-local-repo.sh "$HOME/.m2/repository"
```

如果你的 Maven 本地仓库不在 `~/.m2/repository`，把上面的源目录替换成实际路径即可。

如果本机没有现成缓存，则需要在线预热一次依赖；受网络或镜像限制时，这一步可能需要你自行补执行。

## 推荐验证命令

先显式切到本机 JDK 21，再执行 Maven Wrapper：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -o validate
./mvnw -o package -DskipTests
./mvnw -o test
```

说明：

- 本仓库已通过 `.mvn/maven.config` 固定本地仓库到 `./.mvn/local-repo`
- `-o` 表示离线执行，避免依赖外部仓库波动
- `./scripts/prewarm-local-repo.sh` 会在复制已有缓存后清理 `_remote.repositories`、`*.lastUpdated` 等元数据，避免镜像仓库 ID 变化导致离线解析失败
- 若 `./.mvn/local-repo` 尚未预热完成，可先执行一次在线命令：

```bash
./mvnw package -DskipTests
./mvnw test
```
