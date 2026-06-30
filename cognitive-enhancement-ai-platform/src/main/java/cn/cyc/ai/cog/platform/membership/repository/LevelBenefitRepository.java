package cn.cyc.ai.cog.platform.membership.repository;

import java.util.Optional;

/**
 * 等级权益值仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface LevelBenefitRepository {

    Optional<String> findBenefitValue(Long levelId, String benefitCode);
}
