package cn.cyc.ai.cog.runtime.harness.service;

import cn.cyc.ai.cog.runtime.harness.entity.HarnessReportEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IHarnessReport服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface IHarnessReportService extends IService<HarnessReportEntity> {
    HarnessReportEntity getByHarnessId(String harnessId);
    HarnessReportEntity getLatest();
}
