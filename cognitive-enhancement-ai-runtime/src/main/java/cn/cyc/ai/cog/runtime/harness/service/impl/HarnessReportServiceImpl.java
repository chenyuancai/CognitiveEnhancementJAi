package cn.cyc.ai.cog.runtime.harness.service.impl;

import cn.cyc.ai.cog.runtime.harness.entity.HarnessReportEntity;
import cn.cyc.ai.cog.runtime.harness.mapper.HarnessReportMapper;
import cn.cyc.ai.cog.runtime.harness.service.IHarnessReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * HarnessReportServiceImpl
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class HarnessReportServiceImpl extends ServiceImpl<HarnessReportMapper, HarnessReportEntity>
        implements IHarnessReportService {
    /**
     * 获取人HarnessID。
     *
     * @param harnessId harnessID
     * @return 人HarnessID
     */
    @Override
    public HarnessReportEntity getByHarnessId(String harnessId) {
        return this.lambdaQuery().eq(HarnessReportEntity::getHarnessId, harnessId).one();
    }
    /**
     * 获取Latest。
     * @return Latest
     */
    @Override
    public HarnessReportEntity getLatest() {
        return this.lambdaQuery().orderByDesc(HarnessReportEntity::getStartTime).last("LIMIT 1").one();
    }
}
