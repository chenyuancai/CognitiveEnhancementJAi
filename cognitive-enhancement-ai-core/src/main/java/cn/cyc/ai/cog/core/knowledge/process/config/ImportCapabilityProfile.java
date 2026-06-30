package cn.cyc.ai.cog.core.knowledge.process.config;

/**
 * 导入能力码配置（由 app/admin 进程绑定 YAML）。
 */
public record ImportCapabilityProfile(
        String summary,
        String imageUnderstand,
        String embedding,
        String quiz
) {

    public static ImportCapabilityProfile defaults() {
        return new ImportCapabilityProfile(
                "capability.kb.summary",
                "capability.kb.image",
                "capability.kb.embedding",
                "capability.qa.answer");
    }
}
