package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.platform.membership.entity.LevelBenefitEntity;
import cn.cyc.ai.cog.platform.membership.mapper.LevelBenefitMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DbLevelBenefitRepository implements LevelBenefitRepository {

    private final LevelBenefitMapper levelBenefitMapper;

    public DbLevelBenefitRepository(LevelBenefitMapper levelBenefitMapper) {
        this.levelBenefitMapper = levelBenefitMapper;
    }

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
