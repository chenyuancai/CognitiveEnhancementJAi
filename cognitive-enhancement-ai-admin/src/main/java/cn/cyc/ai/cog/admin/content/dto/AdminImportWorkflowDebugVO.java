package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.util.List;

/**
 * Admin 导入工作流同步调试结果。
 */
@Data
public class AdminImportWorkflowDebugVO {

    private Long contentId;
    private Integer chunkCount;
    private Boolean vectorized;
    private Boolean aiEnriched;
    private String markdownPreview;
    private List<StageVO> stages;

    @Data
    public static class StageVO {
        private String stage;
        private String clientStage;
        private Integer progress;
        private String message;
    }
}
