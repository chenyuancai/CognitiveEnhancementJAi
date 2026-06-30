package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.app.dto.AppContentDetailVO;
import cn.cyc.ai.cog.app.dto.AppContentSummaryVO;
import cn.cyc.ai.cog.app.dto.AppKnowledgePackageTreeVO;
import cn.cyc.ai.cog.app.dto.AppKnowledgeTreeItemVO;
import cn.cyc.ai.cog.app.support.AppReadCache;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackage;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackageItem;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentPageQuery;
import cn.cyc.ai.cog.api.enums.ContentStatus;
import cn.cyc.ai.cog.api.enums.EnableStatus;
import cn.cyc.ai.cog.platform.knowledge.repository.KnowledgePackageRepository;
import cn.cyc.ai.cog.platform.knowledge.service.ContentService;
import cn.cyc.ai.cog.platform.membership.repository.MembershipLevelRepository;
import cn.cyc.ai.cog.platform.membership.support.MembershipLevelAccessSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * C 端知识内容只读服务（已发布内容 + 等级过滤）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppKnowledgeService {

    /** 内容服务 */
    private final ContentService contentService;

    /** 知识包仓储 */
    private final KnowledgePackageRepository knowledgePackageRepository;

    /** 用户上下文服务 */
    private final UserMeContextService userMeContextService;

    /** 会员等级仓储 */
    private final MembershipLevelRepository membershipLevelRepository;

    /** 会员等级可见性辅助 */
    private final MembershipLevelAccessSupport membershipLevelAccessSupport;

    /** 只读缓存 */
    private final AppReadCache appReadCache;

    /**
     * @param contentService                 内容服务
     * @param knowledgePackageRepository     知识包仓储
     * @param userMeContextService           用户上下文服务
     * @param membershipLevelRepository      会员等级仓储
     * @param membershipLevelAccessSupport   等级可见性辅助
     * @param appReadCache                   只读缓存
     */
    public AppKnowledgeService(ContentService contentService,
                               KnowledgePackageRepository knowledgePackageRepository,
                               UserMeContextService userMeContextService,
                               MembershipLevelRepository membershipLevelRepository,
                               MembershipLevelAccessSupport membershipLevelAccessSupport,
                               AppReadCache appReadCache) {
        this.contentService = contentService;
        this.knowledgePackageRepository = knowledgePackageRepository;
        this.userMeContextService = userMeContextService;
        this.membershipLevelRepository = membershipLevelRepository;
        this.membershipLevelAccessSupport = membershipLevelAccessSupport;
        this.appReadCache = appReadCache;
    }

    /**
     * 分页查询已发布内容（按会员等级过滤不可见项）。
     *
     * @param query 分页参数
     * @return 内容摘要分页
     */
    public PageResult<AppContentSummaryVO> pagePublishedContents(ContentPageQuery query) {
        String userLevel = resolveUserLevelCode();
        Map<String, Integer> sortIndex = loadLevelSortIndex();
        query.setStatus(ContentStatus.PUBLISHED.code());
        return contentService.page(query).map(content -> toSummary(content, userLevel, sortIndex, false));
    }

    /**
     * 查询已发布内容详情。
     *
     * @param id 内容 ID
     * @return 内容详情
     */
    public AppContentDetailVO getPublishedContent(Long id) {
        Content content = contentService.detail(id);
        if (!ContentStatus.PUBLISHED.matches(content.status())) {
            throw Errors.of(PlatformErrorCode.CONTENT_NOT_PUBLISHED);
        }
        String userLevel = resolveUserLevelCode();
        Map<String, Integer> sortIndex = loadLevelSortIndex();
        boolean canAccess = membershipLevelAccessSupport.canAccess(userLevel, content.minLevelCode(), sortIndex);
        AppContentDetailVO vo = new AppContentDetailVO();
        vo.setId(content.id());
        vo.setTitle(content.title());
        vo.setContentType(content.contentType());
        vo.setAuthor(content.author());
        vo.setSummary(content.summary());
        vo.setMinLevelCode(content.minLevelCode());
        vo.setLocked(!canAccess);
        if (canAccess) {
            vo.setBody(content.body());
        } else {
            vo.setUpgradeHint("当前会员等级不足，请升级后阅读完整内容");
        }
        return vo;
    }

    /**
     * 查询已启用知识包列表。
     *
     * @return 知识包列表
     */
    public List<AppKnowledgePackageTreeVO> listEnabledPackages() {
        Long tenantId = TenantContext.currentTenantId();
        return knowledgePackageRepository.listEnabled(tenantId).stream()
                .map(this::toPackageSummary)
                .toList();
    }

    /**
     * 查询知识包目录树（仅含当前等级可见内容）。
     *
     * @param packageId 知识包 ID
     * @return 目录树
     */
    public AppKnowledgePackageTreeVO getPackageTree(Long packageId) {
        KnowledgePackage pkg = knowledgePackageRepository.findById(packageId);
        if (!EnableStatus.ENABLED.matches(pkg.status())) {
            throw Errors.of(PlatformErrorCode.KNOWLEDGE_PACKAGE_NOT_ENABLED);
        }
        String userLevel = resolveUserLevelCode();
        Map<String, Integer> sortIndex = loadLevelSortIndex();
        Map<Long, String> contentMinLevel = loadContentMinLevels(knowledgePackageRepository.listItems(packageId));

        AppKnowledgePackageTreeVO tree = toPackageSummary(pkg);
        tree.setItems(buildTree(knowledgePackageRepository.listItems(packageId), 0L,
                userLevel, sortIndex, contentMinLevel));
        return tree;
    }

    /**
     * 构建Tree。
     * @return 构建结果
     */
    private List<AppKnowledgeTreeItemVO> buildTree(List<KnowledgePackageItem> items, Long parentId,
                                                   String userLevel, Map<String, Integer> sortIndex,
                                                   Map<Long, String> contentMinLevel) {
        Map<Long, List<KnowledgePackageItem>> grouped = new LinkedHashMap<>();
        for (KnowledgePackageItem item : items) {
            long parent = item.parentId() == null ? 0L : item.parentId();
            grouped.computeIfAbsent(parent, key -> new ArrayList<>()).add(item);
        }
        return buildChildren(grouped, parentId, userLevel, sortIndex, contentMinLevel);
    }

    /**
     * 构建Children。
     * @return 构建结果
     */
    private List<AppKnowledgeTreeItemVO> buildChildren(Map<Long, List<KnowledgePackageItem>> grouped,
                                                      Long parentId, String userLevel,
                                                      Map<String, Integer> sortIndex,
                                                      Map<Long, String> contentMinLevel) {
        List<KnowledgePackageItem> children = grouped.getOrDefault(parentId, List.of());
        List<AppKnowledgeTreeItemVO> nodes = new ArrayList<>();
        for (KnowledgePackageItem item : children) {
            AppKnowledgeTreeItemVO node = new AppKnowledgeTreeItemVO();
            node.setId(item.id());
            node.setTitle(item.title());
            node.setContentId(item.contentId());
            if (item.contentId() != null) {
                String required = contentMinLevel.get(item.contentId());
                boolean canAccess = membershipLevelAccessSupport.canAccess(userLevel, required, sortIndex);
                node.setLocked(!canAccess);
                if (!canAccess) {
                    node.setContentId(null);
                }
            }
            node.setChildren(buildChildren(grouped, item.id(), userLevel, sortIndex, contentMinLevel));
            nodes.add(node);
        }
        return nodes;
    }

    private Map<Long, String> loadContentMinLevels(List<KnowledgePackageItem> items) {
        Map<Long, String> map = new LinkedHashMap<>();
        for (KnowledgePackageItem item : items) {
            if (item.contentId() == null) {
                continue;
            }
            Content content = contentService.detail(item.contentId());
            if (ContentStatus.PUBLISHED.matches(content.status())) {
                map.put(item.contentId(), content.minLevelCode());
            }
        }
        return map;
    }

    /**
     * 转换为Package摘要。
     *
     * @param pkg pkg
     * @return 转换结果
     */
    private AppKnowledgePackageTreeVO toPackageSummary(KnowledgePackage pkg) {
        AppKnowledgePackageTreeVO vo = new AppKnowledgePackageTreeVO();
        vo.setId(pkg.id());
        vo.setPackageName(pkg.packageName());
        vo.setDescription(pkg.description());
        return vo;
    }

    /**
     * 转换为摘要。
     * @return 转换结果
     */
    private AppContentSummaryVO toSummary(Content content, String userLevel,
                                          Map<String, Integer> sortIndex, boolean hideLocked) {
        boolean canAccess = membershipLevelAccessSupport.canAccess(userLevel, content.minLevelCode(), sortIndex);
        if (hideLocked && !canAccess) {
            return null;
        }
        AppContentSummaryVO vo = new AppContentSummaryVO();
        vo.setId(content.id());
        vo.setTitle(content.title());
        vo.setContentType(content.contentType());
        vo.setAuthor(content.author());
        vo.setSummary(canAccess ? content.summary() : "升级会员后可阅读");
        vo.setMinLevelCode(content.minLevelCode());
        vo.setLocked(!canAccess);
        return vo;
    }

    /**
     * 执行resolve用户等级编码。
     * @return 执行结果
     */
    private String resolveUserLevelCode() {
        UserMeContext context = userMeContextService.buildForCurrentUser();
        if (context.getMembership() == null || !StringUtils.hasText(context.getMembership().getLevelCode())) {
            return "FREE";
        }
        return context.getMembership().getLevelCode();
    }

    private Map<String, Integer> loadLevelSortIndex() {
        Long tenantId = TenantContext.currentTenantId();
        String cacheKey = "level-sort:" + tenantId;
        return appReadCache.get(cacheKey, () ->
                membershipLevelAccessSupport.buildSortIndex(membershipLevelRepository.listEnabled("2C")));
    }
}
