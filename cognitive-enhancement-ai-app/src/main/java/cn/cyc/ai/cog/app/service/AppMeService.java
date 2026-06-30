package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.app.assembler.AppMeVoAssembler;
import cn.cyc.ai.cog.app.dto.AppMeResponse;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import org.springframework.stereotype.Service;

/**
 * C 端当前用户上下文门面（不含 Admin RBAC/菜单）。
 * <p>
 * 聚合逻辑委托 {@link UserMeContextService}，本类仅负责 VO 转换。
 * </p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppMeService {

    /** 平台用户商业上下文聚合服务 */
    private final UserMeContextService userMeContextService;

    /** 平台快照 → C 端 VO 转换器 */
    private final AppMeVoAssembler appMeVoAssembler;

    /**
     * @param userMeContextService 用户上下文聚合服务
     * @param appMeVoAssembler     VO 转换器
     */
    public AppMeService(UserMeContextService userMeContextService, AppMeVoAssembler appMeVoAssembler) {
        this.userMeContextService = userMeContextService;
        this.appMeVoAssembler = appMeVoAssembler;
    }

    /**
     * 构建 C 端当前用户完整上下文。
     *
     * @return 用户、账户、会员与额度摘要
     */
    public AppMeResponse buildMe() {
        return appMeVoAssembler.toResponse(userMeContextService.buildForCurrentUser());
    }
}
