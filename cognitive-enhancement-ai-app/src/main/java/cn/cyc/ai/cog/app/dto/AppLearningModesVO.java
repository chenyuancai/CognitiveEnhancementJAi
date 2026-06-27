package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * C 端学习模式可用性。
 */
@Data
public class AppLearningModesVO {

    private List<AppLearningModeItemVO> modes = new ArrayList<>();
}
