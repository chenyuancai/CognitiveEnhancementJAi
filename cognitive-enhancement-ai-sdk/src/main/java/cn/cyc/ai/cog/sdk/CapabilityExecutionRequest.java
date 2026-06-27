package cn.cyc.ai.cog.sdk;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SDK 能力执行请求。
 *
 * @param capabilityCode 能力编码
 * @param input          输入参数
 * @param parameters     执行参数
 * @author cyc
 */
public record CapabilityExecutionRequest(String capabilityCode,
                                         Map<String, Object> input,
                                         Map<String, Object> parameters) {

    public CapabilityExecutionRequest {
        capabilityCode = Objects.requireNonNull(capabilityCode, "capabilityCode 不能为空");
        input = Map.copyOf(input == null ? Map.of() : input);
        parameters = Map.copyOf(parameters == null ? Map.of() : parameters);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 能力执行请求构造器。
     */
    public static final class Builder {
        private String capabilityCode;
        private Map<String, Object> input = new LinkedHashMap<>();
        private Map<String, Object> parameters = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder capabilityCode(String capabilityCode) {
            this.capabilityCode = capabilityCode;
            return this;
        }

        public Builder input(Map<String, Object> input) {
            this.input = new LinkedHashMap<>(input == null ? Map.of() : input);
            return this;
        }

        public Builder input(String key, Object value) {
            this.input.put(key, value);
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = new LinkedHashMap<>(parameters == null ? Map.of() : parameters);
            return this;
        }

        public Builder parameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        public CapabilityExecutionRequest build() {
            return new CapabilityExecutionRequest(capabilityCode, input, parameters);
        }
    }
}
