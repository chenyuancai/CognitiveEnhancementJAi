package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.platform.membership.entity.LevelBenefitEntity;
import cn.cyc.ai.cog.platform.membership.mapper.LevelBenefitMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Db等级权益仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbLevelBenefitRepository implements LevelBenefitRepository {

    /** 等级权益Mapper。 */
    private final LevelBenefitMapper levelBenefitMapper;

    /**
     * 创建Db等级权益仓储。
     *
     * @param levelBenefitMapper 等级权益Mapper
     */
    public DbLevelBenefitRepository(LevelBenefitMapper levelBenefitMapper) {
        this.levelBenefitMapper = levelBenefitMapper;
    }

    /**
     * 查找权益值。
     *
     * @param levelId 等级ID
     * @param benefitCode 权益编码
     * @return 查找结果
     */
    @Override
    public Optional<String> findBenefitValue(Long levelId, String benefitCode) {
        if (levelId == null || benefitCode == null) {
            return Optional.empty();
        }
        LevelBenefitEntity entity = levelBenefitMapper.selectOne(new LambdaQueryWrapper<LevelBenefitEntity>()
                .eq(LevelBenefitEntity::getLevelId, levelId)
                .eq(LevelBenefitEntity::getBenefitCode, benefitCode)
                .last("LIMIT 1"));
        return entity == null ? Optional.empty() : Optional.ofNullable(entity.getBenefitValue());
    }
}
