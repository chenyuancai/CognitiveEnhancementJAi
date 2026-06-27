package cn.cyc.ai.cog.runtime.knowledge.service;

import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragment;
import cn.cyc.ai.cog.runtime.knowledge.domain.KnowledgeFragmentStatus;
import cn.cyc.ai.cog.runtime.knowledge.domain.ScenarioKnowledgeBinding;
import cn.cyc.ai.cog.runtime.knowledge.spi.KnowledgeFragmentRepository;
import cn.cyc.ai.cog.runtime.knowledge.spi.ScenarioKnowledgeBindingRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 知识检索服务。
 *
 * @author cyc
 */
@Service
public class KnowledgeRetrievalService {

    /**
     * 默认检索上限。
     */
    private static final int DEFAULT_LIMIT = 5;

    /**
     * 场景知识绑定仓储。
     */
    private final ScenarioKnowledgeBindingRepository scenarioKnowledgeBindingRepository;

    /**
     * 知识片段仓储。
     */
    private final KnowledgeFragmentRepository knowledgeFragmentRepository;

    /**
     * 构造知识检索服务。
     *
     * @param scenarioKnowledgeBindingRepository 场景知识绑定仓储
     * @param knowledgeFragmentRepository        知识片段仓储
     */
    public KnowledgeRetrievalService(ScenarioKnowledgeBindingRepository scenarioKnowledgeBindingRepository,
                                     KnowledgeFragmentRepository knowledgeFragmentRepository) {
        this.scenarioKnowledgeBindingRepository = scenarioKnowledgeBindingRepository;
        this.knowledgeFragmentRepository = knowledgeFragmentRepository;
    }

    /**
     * 按场景检索知识片段。
     *
     * @param scenarioCode 场景编码
     * @param query        检索关键词
     * @param limit        返回上限，默认 5
     * @return 匹配的知识片段列表
     */
    public List<KnowledgeFragment> retrieve(String scenarioCode, String query, Integer limit) {
        int effectiveLimit = limit == null || limit <= 0 ? DEFAULT_LIMIT : limit;
        String normalizedQuery = query == null ? "" : query.trim();
        boolean hasQuery = StringUtils.hasText(normalizedQuery);
        String queryLower = normalizedQuery.toLowerCase(Locale.ROOT);

        List<ScenarioKnowledgeBinding> bindings = scenarioKnowledgeBindingRepository.findByScenarioCode(scenarioCode).stream()
                .filter(ScenarioKnowledgeBinding::enabled)
                .toList();

        Map<String, Integer> knowledgePriorities = new LinkedHashMap<>();
        for (ScenarioKnowledgeBinding binding : bindings) {
            knowledgePriorities.putIfAbsent(binding.knowledgeCode(), binding.priority());
        }

        List<ScoredFragment> scoredFragments = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : knowledgePriorities.entrySet()) {
            int priority = entry.getValue();
            for (KnowledgeFragment fragment : knowledgeFragmentRepository.findByKnowledgeCode(entry.getKey())) {
                if (fragment.status() != KnowledgeFragmentStatus.ENABLED) {
                    continue;
                }
                int matchScore = hasQuery ? computeMatchScore(fragment, queryLower) : 1;
                if (!hasQuery || matchScore > 0) {
                    scoredFragments.add(new ScoredFragment(fragment, priority, matchScore));
                }
            }
        }

        return scoredFragments.stream()
                .sorted(Comparator.comparingInt(ScoredFragment::priority)
                        .thenComparing(Comparator.comparingInt(ScoredFragment::matchScore).reversed())
                        .thenComparing(scoredFragment -> scoredFragment.fragment().recordedAt(), Comparator.reverseOrder()))
                .limit(effectiveLimit)
                .map(ScoredFragment::fragment)
                .toList();
    }

    private int computeMatchScore(KnowledgeFragment fragment, String queryLower) {
        String title = fragment.title() == null ? "" : fragment.title().toLowerCase(Locale.ROOT);
        String content = fragment.content() == null ? "" : fragment.content().toLowerCase(Locale.ROOT);
        int score = 0;
        if (title.contains(queryLower)) {
            score += 2;
        }
        if (content.contains(queryLower)) {
            score += 1;
        }
        return score;
    }

    private record ScoredFragment(KnowledgeFragment fragment, int priority, int matchScore) {
    }
}
