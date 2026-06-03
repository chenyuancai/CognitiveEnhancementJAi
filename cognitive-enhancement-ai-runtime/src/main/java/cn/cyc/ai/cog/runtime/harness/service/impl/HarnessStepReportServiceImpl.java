package cn.cyc.ai.cog.runtime.harness.service.impl;

import cn.cyc.ai.cog.runtime.harness.entity.HarnessStepReportEntity;
import cn.cyc.ai.cog.runtime.harness.mapper.HarnessStepReportMapper;
import cn.cyc.ai.cog.runtime.harness.service.IHarnessStepReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HarnessStepReportServiceImpl extends ServiceImpl<HarnessStepReportMapper, HarnessStepReportEntity>
        implements IHarnessStepReportService {
}
