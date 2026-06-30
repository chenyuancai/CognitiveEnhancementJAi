package cn.cyc.ai.cog.admin.workbench.dto;

import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 趋势曲线序列。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class TrendSeries {

    /** metric。 */
    private String metric;
    /** points。 */
    private List<DailyPoint> points = new ArrayList<>();
}
