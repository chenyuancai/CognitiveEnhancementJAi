package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.app.dto.AppInAppMessageVO;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.app.support.AppOpsLabelSupport;
import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;
import cn.cyc.ai.cog.platform.operations.service.InAppMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * C端InC端消息服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppInAppMessageService {

    /** inC端消息服务。 */
    private final InAppMessageService inAppMessageService;

    /**
     * 创建C端InC端消息服务。
     *
     * @param inAppMessageService inC端消息服务
     */
    public AppInAppMessageService(InAppMessageService inAppMessageService) {
        this.inAppMessageService = inAppMessageService;
    }

    /**
     * 查询Item列表。
     *
     * @param read read
     * @return 结果列表
     */
    public List<AppInAppMessageVO> list(Boolean read) {
        Long userId = UserContext.currentUserId();
        return inAppMessageService.listForUser(TenantContext.currentTenantId(), userId, read)
                .stream().map(this::toVo).toList();
    }

    /**
     * 执行markRead。
     *
     * @param messageId 消息 ID
     * @return 执行结果
     */
    public AppInAppMessageVO markRead(Long messageId) {
        InAppMessage message = inAppMessageService.markRead(
                TenantContext.currentTenantId(), UserContext.currentUserId(), messageId);
        return toVo(message);
    }

    /**
     * 转换为Vo。
     *
     * @param message 消息
     * @return 转换结果
     */
    private AppInAppMessageVO toVo(InAppMessage message) {
        AppInAppMessageVO vo = new AppInAppMessageVO();
        vo.setId(message.id());
        vo.setTemplateCode(message.templateCode());
        vo.setTitle(message.title());
        vo.setContent(message.content());
        vo.setRead(message.read());
        vo.setCategoryLabel(AppOpsLabelSupport.messageCategoryLabel(message.templateCode()));
        vo.setCreateTime(message.createTime());
        return vo;
    }
}
