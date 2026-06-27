package cn.cyc.ai.cog.admin.workbench.dto;

import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 趋势曲线序列。
 */
@Data
public class TrendSeries {

    private String metric;
    private List<DailyPoint> points = new ArrayList<>();
}
