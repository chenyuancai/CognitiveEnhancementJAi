package cn.cyc.ai.cog.runtime.harness.spi;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;

/**
 * Harness 报告仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface HarnessReportRepository {

    /**
     * 保存报告。
     *
     * @param report Harness 报告
     */
    void save(HarnessReport report);

    /**
     * 按 ID 查询报告。
     *
     * @param harnessId Harness 标识
     * @return 报告（可能为空）
     */
    Optional<HarnessReport> findById(String harnessId);

    /**
     * 查询最新报告。
     *
     * @return 最新报告（可能为空）
     */
    Optional<HarnessReport> findLatest();

    /**
     * 查询全部报告（按时间倒序）。
     *
     * @return 报告列表
     */
    List<HarnessReport> findAll();

    /**
     * 分页查询报告（按时间倒序）。
     *
     * @param page 分页参数
     * @return 分页结果
     */
    Page<HarnessReport> findPage(Page<HarnessReport> page);

    /**
     * 按条件分页查询报告（按开始时间倒序）。
     *
     * @param page  分页参数
     * @param query 筛选条件
     * @return 分页结果
     */
    Page<HarnessReport> findPage(Page<HarnessReport> page, HarnessReportQuery query);
}
