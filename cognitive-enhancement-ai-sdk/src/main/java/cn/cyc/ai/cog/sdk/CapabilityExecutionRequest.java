package cn.cyc.ai.cog.sdk;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SDK 能力执行请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CapabilityExecutionRequest(String capabilityCode,
                                         Map<String, Object> input,
                                         Map<String, Object> parameters) {

    public CapabilityExecutionRequest {
        capabilityCode = Objects.requireNonNull(capabilityCode, "capabilityCode 不能为空");
        input = Map.copyOf(input == null ? Map.of() : input);
        parameters = Map.copyOf(parameters == null ? Map.of() : parameters);
    }

    /**
     * 构建er。
     * @return 构建结果
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 能力执行请求构造器。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public static final class Builder {
        /** 能力编码。 */
        private String capabilityCode;
        private Map<String, Object> input = new LinkedHashMap<>();
        private Map<String, Object> parameters = new LinkedHashMap<>();

        /**
         * 创建Builder。
         */
        private Builder() {
        }

        /**
         * 执行能力编码。
         *
         * @param capabilityCode 能力编码
         * @return 执行结果
         */
        public Builder capabilityCode(String capabilityCode) {
            this.capabilityCode = capabilityCode;
            return this;
        }

        /**
         * 执行输入。
         *
         * @param input 输入
         * @return 执行结果
         */
        public Builder input(Map<String, Object> input) {
            this.input = new LinkedHashMap<>(input == null ? Map.of() : input);
            return this;
        }

        /**
         * 执行输入。
         *
         * @param key 键
         * @param value 值
         * @return 执行结果
         */
        public Builder input(String key, Object value) {
            this.input.put(key, value);
            return this;
        }

        /**
         * 执行parameters。
         *
         * @param parameters parameters
         * @return 执行结果
         */
        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = new LinkedHashMap<>(parameters == null ? Map.of() : parameters);
            return this;
        }

        /**
         * 执行parameter。
         *
         * @param key 键
         * @param value 值
         * @return 执行结果
         */
        public Builder parameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        /**
         * 构建Object。
         * @return 构建结果
         */
        public CapabilityExecutionRequest build() {
            return new CapabilityExecutionRequest(capabilityCode, input, parameters);
        }
    }
}
