package cn.cyc.ai.cog.core.knowledge.process;

import java.util.EnumMap;
import java.util.Map;

/**
 * 导入业务类型默认配置。
 */
public final class ImportBizTypeProfiles {

    private static final Map<ImportBizType, ImportBizTypeProfile> PROFILES = new EnumMap<>(ImportBizType.class);

    static {
        PROFILES.put(ImportBizType.KNOWLEDGE_DOCUMENT, new ImportBizTypeProfile(true, true, false));
        PROFILES.put(ImportBizType.KNOWLEDGE_URL, new ImportBizTypeProfile(true, true, false));
        PROFILES.put(ImportBizType.COURSE_HANDOUT, new ImportBizTypeProfile(true, true, false));
        PROFILES.put(ImportBizType.EXAM_PAPER, new ImportBizTypeProfile(false, false, true));
        PROFILES.put(ImportBizType.MISTAKE_ARCHIVE, new ImportBizTypeProfile(false, false, false));
        PROFILES.put(ImportBizType.PRACTICE_SOURCE, new ImportBizTypeProfile(true, false, true));
    }

    private ImportBizTypeProfiles() {
    }

    public static ImportBizTypeProfile of(ImportBizType type) {
        return PROFILES.getOrDefault(type, PROFILES.get(ImportBizType.KNOWLEDGE_DOCUMENT));
    }
}
