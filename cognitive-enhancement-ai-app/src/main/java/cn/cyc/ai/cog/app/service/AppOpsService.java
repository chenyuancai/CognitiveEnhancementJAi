package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.app.dto.AppAnnouncementVO;
import cn.cyc.ai.cog.app.dto.AppBannerVO;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.account.dto.UserMeContext;
import cn.cyc.ai.cog.platform.account.service.UserMeContextService;
import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import cn.cyc.ai.cog.platform.operations.domain.Banner;
import cn.cyc.ai.cog.platform.operations.repository.AnnouncementRepository;
import cn.cyc.ai.cog.platform.operations.repository.BannerRepository;
import cn.cyc.ai.cog.platform.operations.support.AnnouncementAudienceSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * C 端运营投放只读服务。
 */
@Service
public class AppOpsService {

    private final BannerRepository bannerRepository;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementAudienceSupport announcementAudienceSupport;
    private final UserMeContextService userMeContextService;

    public AppOpsService(BannerRepository bannerRepository,
                         AnnouncementRepository announcementRepository,
                         AnnouncementAudienceSupport announcementAudienceSupport,
                         UserMeContextService userMeContextService) {
        this.bannerRepository = bannerRepository;
        this.announcementRepository = announcementRepository;
        this.announcementAudienceSupport = announcementAudienceSupport;
        this.userMeContextService = userMeContextService;
    }

    public List<AppBannerVO> listActiveBanners(String position) {
        String effectivePosition = StringUtils.hasText(position) ? position.trim() : "HOME_TOP";
        return bannerRepository.listActiveByPosition(effectivePosition, LocalDateTime.now()).stream()
                .map(this::toBannerVo)
                .toList();
    }

    public List<AppAnnouncementVO> listPublishedAnnouncements() {
        AudienceContext audience = resolveAudience();
        return announcementRepository.listPublished().stream()
                .filter(item -> announcementAudienceSupport.isVisible(item, audience.userId(), audience.levelCode()))
                .map(this::toAnnouncementVo)
                .toList();
    }

    private AudienceContext resolveAudience() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            return new AudienceContext(null, null);
        }
        try {
            UserMeContext context = userMeContextService.buildForUserId(userId);
            String levelCode = context.getMembership() == null ? "FREE" : context.getMembership().getLevelCode();
            if (!StringUtils.hasText(levelCode)) {
                levelCode = "FREE";
            }
            return new AudienceContext(userId, levelCode);
        } catch (RuntimeException ex) {
            return new AudienceContext(userId, "FREE");
        }
    }

    private AppBannerVO toBannerVo(Banner banner) {
        AppBannerVO vo = new AppBannerVO();
        vo.setId(banner.id());
        vo.setTitle(banner.title());
        vo.setImageUrl(banner.imageUrl());
        vo.setLinkUrl(banner.linkUrl());
        vo.setPosition(banner.position());
        vo.setSortNo(banner.sortNo());
        vo.setStartTime(banner.startTime());
        vo.setEndTime(banner.endTime());
        return vo;
    }

    private AppAnnouncementVO toAnnouncementVo(Announcement announcement) {
        AppAnnouncementVO vo = new AppAnnouncementVO();
        vo.setId(announcement.id());
        vo.setTitle(announcement.title());
        vo.setBody(announcement.body());
        vo.setPublishAt(announcement.publishAt());
        return vo;
    }

    private record AudienceContext(Long userId, String levelCode) {
    }
}
