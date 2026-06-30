package cn.cyc.ai.cog.runtime.importkb;

import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.knowledge.process.config.ImportCapabilityProfile;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportAiPort;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 导入链路 AI 服务：经 RuntimeHarness 调用 center 配置的能力码。
 */
@Service
public class KbImportAiService implements ImportAiPort {

    private static final Logger log = LoggerFactory.getLogger(KbImportAiService.class);

    private final ObjectProvider<RuntimeHarness> runtimeHarnessProvider;
    private final ImportCapabilityProfile capabilityProfile;

    public KbImportAiService(ObjectProvider<RuntimeHarness> runtimeHarnessProvider,
                             ImportCapabilityProfile capabilityProfile) {
        this.runtimeHarnessProvider = runtimeHarnessProvider;
        this.capabilityProfile = capabilityProfile == null
                ? ImportCapabilityProfile.defaults()
                : capabilityProfile;
    }

    @Override
    public String summarize(String title, String markdown) {
        RuntimeHarness harness = runtimeHarnessProvider.getIfAvailable();
        if (harness == null || !StringUtils.hasText(markdown)) {
            return fallbackSummary(markdown);
        }
        try {
            String prompt = """
                    请为以下学习资料生成不超过300字的中文摘要，只输出摘要正文。
                    标题：%s
                    正文：
                    %s
                    """.formatted(title == null ? "" : title, abbreviate(markdown, 6000));
            CapabilityExecuteResponse response = harness.execute(new CapabilityExecuteRequest(
                    capabilityProfile.summary(),
                    Map.of("question", prompt, "title", title == null ? "" : title),
                    Map.of("conversationEnabled", false, "summaryMode", true)));
            String text = extractText(response);
            return StringUtils.hasText(text) ? text.trim() : fallbackSummary(markdown);
        } catch (Exception ex) {
            log.warn("import kb summary failed, fallback", ex);
            return fallbackSummary(markdown);
        }
    }

    @Override
    public List<Float> embed(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        RuntimeHarness harness = runtimeHarnessProvider.getIfAvailable();
        if (harness == null) {
            return pseudoEmbedding(text);
        }
        try {
            CapabilityExecuteResponse response = harness.execute(new CapabilityExecuteRequest(
                    capabilityProfile.embedding(),
                    Map.of("text", abbreviate(text, 4000)),
                    Map.of()));
            Object vector = extractOutput(response).get("embedding");
            if (vector instanceof List<?> list && !list.isEmpty()) {
                return list.stream().map(item -> Float.parseFloat(String.valueOf(item))).toList();
            }
        } catch (Exception ex) {
            log.warn("import kb embedding failed, fallback pseudo", ex);
        }
        return pseudoEmbedding(text);
    }

    @Override
    public Map<String, Object> generateQuizDraft(String title, String markdown) {
        RuntimeHarness harness = runtimeHarnessProvider.getIfAvailable();
        if (harness == null) {
            return Map.of();
        }
        try {
            CapabilityExecuteResponse response = harness.execute(new CapabilityExecuteRequest(
                    capabilityProfile.quiz(),
                    Map.of("title", title == null ? "" : title, "content", abbreviate(markdown, 6000)),
                    Map.of("quizDraft", true)));
            return extractOutput(response);
        } catch (Exception ex) {
            log.warn("import kb quiz draft failed", ex);
            return Map.of();
        }
    }

    private String fallbackSummary(String markdown) {
        if (!StringUtils.hasText(markdown)) {
            return "";
        }
        String plain = markdown.replaceAll("[#>*`_\\-]", " ").replaceAll("\\s+", " ").trim();
        return plain.length() <= 300 ? plain : plain.substring(0, 300);
    }

    private List<Float> pseudoEmbedding(String text) {
        int hash = text.hashCode();
        return List.of(
                (float) ((hash & 0xFF) / 255.0),
                (float) (((hash >> 8) & 0xFF) / 255.0),
                (float) (((hash >> 16) & 0xFF) / 255.0));
    }

    private String extractText(CapabilityExecuteResponse response) {
        Map<String, Object> output = extractOutput(response);
        Object answer = output.get("answer");
        if (answer != null) {
            return String.valueOf(answer);
        }
        Object text = output.get("text");
        return text == null ? "" : String.valueOf(text);
    }

    private Map<String, Object> extractOutput(CapabilityExecuteResponse response) {
        if (response == null) {
            return Map.of();
        }
        ExecutionResult result = response.result();
        if (result == null || result.output() == null) {
            return Map.of();
        }
        return result.output();
    }

    private String abbreviate(String text, int max) {
        if (text == null || text.length() <= max) {
            return text == null ? "" : text;
        }
        return text.substring(0, max);
    }
}
