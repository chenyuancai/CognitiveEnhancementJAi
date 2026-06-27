package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.app.dto.AppInAppMessageVO;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;
import cn.cyc.ai.cog.platform.operations.service.InAppMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppInAppMessageService {

    private final InAppMessageService inAppMessageService;

    public AppInAppMessageService(InAppMessageService inAppMessageService) {
        this.inAppMessageService = inAppMessageService;
    }

    public List<AppInAppMessageVO> list(Boolean read) {
        Long userId = UserContext.currentUserId();
        return inAppMessageService.listForUser(TenantContext.currentTenantId(), userId, read)
                .stream().map(this::toVo).toList();
    }

    public AppInAppMessageVO markRead(Long messageId) {
        InAppMessage message = inAppMessageService.markRead(
                TenantContext.currentTenantId(), UserContext.currentUserId(), messageId);
        return toVo(message);
    }

    private AppInAppMessageVO toVo(InAppMessage message) {
        AppInAppMessageVO vo = new AppInAppMessageVO();
        vo.setId(message.id());
        vo.setTemplateCode(message.templateCode());
        vo.setTitle(message.title());
        vo.setContent(message.content());
        vo.setRead(message.read());
        vo.setCreateTime(message.createTime());
        return vo;
    }
}
