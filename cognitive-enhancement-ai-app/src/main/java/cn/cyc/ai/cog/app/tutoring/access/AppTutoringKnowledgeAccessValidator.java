package cn.cyc.ai.cog.app.tutoring.access;

import cn.cyc.ai.cog.api.enums.ContentStatus;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringReferences;
import cn.cyc.ai.cog.app.support.AppReadCache;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringConstants;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.service.ContentService;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.support.MembershipLevelAccessSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * C 端 AI 助手知识引用权限校验器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringKnowledgeAccessValidator {

    /** 知识内容服务。 */
    private final ContentService contentService;

    /** 当前用户上下文服务。 */
    private final UserMeContextService userMeContextService;

    /** 会员等级仓储。 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** 会员等级访问支持组件。 */
    private final MembershipLevelAccessSupport membershipLevelAccessSupport;

    /** 应用只读缓存。 */
    private final AppReadCache appReadCache;

    /**
     * 构造知识引用权限校验器。
     *
     * @param contentService                知识内容服务
     * @param userMeContextService          当前用户上下文服务
     * @param membershipLevelRepository     会员等级仓储
     * @param membershipLevelAccessSupport  会员等级访问支持组件
     * @param appReadCache                  应用只读缓存
     */
    public AppTutoringKnowledgeAccessValidator(ContentService contentService,
                                               UserMeContextService userMeContextService,
                                               MembershipLevelRepository membershipLevelRepository,
                                               MembershipLevelAccessSupport membershipLevelAccessSupport,
                                               AppReadCache appReadCache) {
        this.contentService = contentService;
        this.userMeContextService = userMeContextService;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipLevelAccessSupport = membershipLevelAccessSupport;
        this.appReadCache = appReadCache;
    }

    /**
     * 校验引用知识内容是否对当前用户可访问。
     *
     * @param references 引用上下文
     * @throws cn.cyc.ai.cog.common.exception.PlatformException 内容未发布或等级不足时抛出
     */
    public void validate(AppTutoringReferences references) {
        if (references == null || CollectionUtils.isEmpty(references.getKnowledgeIds())) {
            return;
        }
        String userLevel = resolveUserLevelCode();
        Map<String, Integer> sortIndex = loadLevelSortIndex();
        for (String knowledgeId : references.getKnowledgeIds()) {
            Long contentId = parseContentId(knowledgeId);
            Content content = contentService.detail(contentId);
            if (!ContentStatus.PUBLISHED.matches(content.status())) {
                throw Errors.of(PlatformErrorCode.FORBIDDEN, "引用内容未发布或不可访问");
            }
            if (!membershipLevelAccessSupport.canAccess(userLevel, content.minLevelCode(), sortIndex)) {
                throw Errors.of(PlatformErrorCode.FORBIDDEN, "当前会员等级无法引用该知识内容");
            }
        }
    }

    /**
     * 将知识引用 ID 解析为内容主键。
     *
     * @param knowledgeId 知识引用 ID
     * @return 内容主键
     */
    private Long parseContentId(String knowledgeId) {
        if (!StringUtils.hasText(knowledgeId)) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "知识引用 ID 无效");
        }
        try {
            return Long.parseLong(knowledgeId.trim());
        } catch (NumberFormatException ex) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "知识引用 ID 无效: " + knowledgeId);
        }
    }

    /**
     * 解析当前用户的会员等级编码。
     *
     * @return 会员等级编码
     */
    private String resolveUserLevelCode() {
        UserMeContext context = userMeContextService.buildForCurrentUser();
        if (context.getMembership() == null || !StringUtils.hasText(context.getMembership().getLevelCode())) {
            return "FREE";
        }
        return context.getMembership().getLevelCode();
    }

    /**
     * 加载会员等级排序索引（带租户级缓存）。
     *
     * @return 等级编码到排序值的映射
     */
    private Map<String, Integer> loadLevelSortIndex() {
        Long tenantId = TenantContext.currentTenantId();
        String cacheKey = "level-sort:" + tenantId;
        return appReadCache.get(cacheKey, () ->
                membershipLevelAccessSupport.buildSortIndex(
                        membershipLevelRepository.listEnabled(AppTutoringConstants.DEFAULT_SEGMENT)));
    }
}
