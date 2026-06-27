package cn.cyc.ai.cog.sdk;

import java.time.Duration;
import java.util.Objects;

/**
 * 平台 SDK 客户端配置。
 *
 * @author cyc
 */
public record CogSdkClientConfig(String baseUrl,
                                 String bearerToken,
                                 String tenantCode,
                                 String traceId,
                                 Duration timeout) {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    public CogSdkClientConfig {
        baseUrl = normalizeBaseUrl(Objects.requireNonNull(baseUrl, "baseUrl 不能为空"));
        timeout = timeout == null ? DEFAULT_TIMEOUT : timeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static String normalizeBaseUrl(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("baseUrl 不能为空");
        }
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    /**
     * SDK 配置构造器。
     */
    public static final class Builder {
        private String baseUrl;
        private String bearerToken;
        private String tenantCode;
        private String traceId;
        private Duration timeout;

        private Builder() {
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder bearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
            return this;
        }

        public Builder tenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public CogSdkClientConfig build() {
            return new CogSdkClientConfig(baseUrl, bearerToken, tenantCode, traceId, timeout);
        }
    }
}
