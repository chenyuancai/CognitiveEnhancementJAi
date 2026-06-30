package cn.cyc.ai.cog.admin.content.service;

import cn.cyc.ai.cog.admin.content.dto.AdminImportWorkflowDebugRequest;
import cn.cyc.ai.cog.admin.content.dto.AdminImportWorkflowDebugVO;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.core.knowledge.process.ImportBizType;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import cn.cyc.ai.cog.runtime.importkb.ImportWorkflowRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Admin 导入工作流调试服务（同步执行，不写 qz_app_import_task）。
 */
@Service
public class AdminImportWorkflowService {

    private final ImportWorkflowRunner importWorkflowRunner;

    public AdminImportWorkflowService(ImportWorkflowRunner importWorkflowRunner) {
        this.importWorkflowRunner = importWorkflowRunner;
    }

    public AdminImportWorkflowDebugVO debugSync(AdminImportWorkflowDebugRequest request) {
        ImportBizType bizType = ImportBizType.fromCode(request.getImportBizType());
        Long tenantId = request.getTenantId() != null ? request.getTenantId() : TenantContext.currentTenantId();
        String fileUrl = StringUtils.hasText(request.getFileUrl())
                ? request.getFileUrl()
                : PlatformFileClient.toBaseFileUrl(request.getFileId());

        ImportWorkflowState state = ImportWorkflowRunner.buildState(
                tenantId,
                request.getUserId(),
                "admin-debug-" + System.currentTimeMillis(),
                bizType,
                request.getFileId(),
                fileUrl,
                request.getFileName(),
                request.getTitle(),
                Boolean.TRUE.equals(request.getAiEnhanced()),
                Boolean.TRUE.equals(request.getAutoQuiz()));

        ImportWorkflowRunner.ImportWorkflowRunResult outcome = importWorkflowRunner.run(state);
        return toVo(outcome);
    }

    private AdminImportWorkflowDebugVO toVo(ImportWorkflowRunner.ImportWorkflowRunResult outcome) {
        AdminImportWorkflowDebugVO vo = new AdminImportWorkflowDebugVO();
        vo.setContentId(outcome.workflow().contentId());
        vo.setChunkCount(outcome.workflow().chunkCount());
        vo.setVectorized(outcome.workflow().vectorized());
        vo.setAiEnriched(outcome.workflow().aiEnriched());
        vo.setMarkdownPreview(outcome.markdownPreview());
        vo.setStages(outcome.stages().stream().map(stage -> {
            AdminImportWorkflowDebugVO.StageVO item = new AdminImportWorkflowDebugVO.StageVO();
            item.setStage(stage.stage());
            item.setClientStage(stage.clientStage());
            item.setProgress(stage.progress());
            item.setMessage(stage.message());
            return item;
        }).toList());
        return vo;
    }
}
