package cn.cyc.ai.cog.platform.membership.repository;

import java.util.Optional;

/**
 * 等级权益值仓储。
 */
public interface LevelBenefitRepository {

    Optional<String> findBenefitValue(Long levelId, String benefitCode);
}
