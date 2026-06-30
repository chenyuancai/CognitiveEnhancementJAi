package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * C 端学习模式可用性。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppLearningModesVO {

    /** modes。 */
    private List<AppLearningModeItemVO> modes = new ArrayList<>();
}
