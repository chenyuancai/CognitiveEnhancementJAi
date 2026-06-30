package cn.cyc.ai.cog.runtime.importkb.config;

import cn.cyc.ai.cog.core.knowledge.process.config.ImportCapabilityProfile;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 导入工作流能力码配置（与 center 种子能力对齐）。
 */
@ConfigurationProperties(prefix = "cog.import")
public class ImportWorkflowProperties {

    private ImportCapabilities capabilities = new ImportCapabilities();

    public ImportCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(ImportCapabilities capabilities) {
        this.capabilities = capabilities == null ? new ImportCapabilities() : capabilities;
    }

    public ImportCapabilityProfile toProfile() {
        return new ImportCapabilityProfile(
                capabilities.getSummary(),
                capabilities.getImageUnderstand(),
                capabilities.getEmbedding(),
                capabilities.getQuiz());
    }

    public static class ImportCapabilities {
        private String summary = "capability.kb.summary";
        private String imageUnderstand = "capability.kb.image";
        private String embedding = "capability.kb.embedding";
        private String quiz = "capability.qa.answer";

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getImageUnderstand() {
            return imageUnderstand;
        }

        public void setImageUnderstand(String imageUnderstand) {
            this.imageUnderstand = imageUnderstand;
        }

        public String getEmbedding() {
            return embedding;
        }

        public void setEmbedding(String embedding) {
            this.embedding = embedding;
        }

        public String getQuiz() {
            return quiz;
        }

        public void setQuiz(String quiz) {
            this.quiz = quiz;
        }
    }
}
