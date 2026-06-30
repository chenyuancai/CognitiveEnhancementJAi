package cn.cyc.ai.cog.sdk;

import java.time.Duration;
import java.util.Objects;

/**
 * 平台 SDK 客户端配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CogSdkClientConfig(String baseUrl,
                                 String bearerToken,
                                 String tenantCode,
                                 String traceId,
                                 Duration timeout) {

    /** 默认TIMEOUT。 */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    public CogSdkClientConfig {
        baseUrl = normalizeBaseUrl(Objects.requireNonNull(baseUrl, "baseUrl 不能为空"));
        timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;
    }

    /**
     * 构建er。
     * @return 构建结果
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行normalizeBase地址。
     *
     * @param value 值
     * @return 执行结果
     */
    private static String normalizeBaseUrl(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("baseUrl 不能为空");
        }
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    /**
     * SDK 配置构造器。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public static final class Builder {
        /** base地址。 */
        private String baseUrl;
        /** bearer令牌。 */
        private String bearerToken;
        /** 租户编码。 */
        private String tenantCode;
        /** 链路 Trace ID */
        private String traceId;
        /** timeout。 */
        private Duration timeout;

        /**
         * 创建Builder。
         */
        private Builder() {
        }

        /**
         * 执行base地址。
         *
         * @param baseUrl base地址
         * @return 执行结果
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * 执行bearer令牌。
         *
         * @param bearerToken bearer令牌
         * @return 执行结果
         */
        public Builder bearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
            return this;
        }

        /**
         * 执行租户编码。
         *
         * @param tenantCode 租户编码
         * @return 执行结果
         */
        public Builder tenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
            return this;
        }

        /**
         * 执行链路 Trace ID。
         *
         * @param traceId 链路 Trace ID
         * @return 执行结果
         */
        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        /**
         * 执行timeout。
         *
         * @param timeout timeout
         * @return 执行结果
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * 构建Object。
         * @return 构建结果
         */
        public CogSdkClientConfig build() {
            return new CogSdkClientConfig(baseUrl, bearerToken, tenantCode, traceId, timeout);
        }
    }
}
