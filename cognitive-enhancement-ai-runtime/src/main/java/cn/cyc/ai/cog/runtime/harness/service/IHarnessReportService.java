package cn.cyc.ai.cog.runtime.harness.service;

import cn.cyc.ai.cog.runtime.harness.entity.HarnessReportEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IHarnessReportService extends IService<HarnessReportEntity> {
    HarnessReportEntity getByHarnessId(String harnessId);
    HarnessReportEntity getLatest();
}
