package cn.cyc.ai.cog.platform.operations.support;

import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 公告受众匹配：会员等级 / 用户群定向。
 * <p>两列均为空时全员可见；任一非空时，登录用户满足等级或用户 ID 任一命中即可见。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AnnouncementAudienceSupport {

    /**
     * 判断公告是否对当前用户可见。
     *
     * @param announcement 公告
     * @param userId       当前用户 ID，未登录为 null
     * @param levelCode    当前用户会员等级编码，未登录为 null
     * @return 是否可见
     */
    public boolean isVisible(Announcement announcement, Long userId, String levelCode) {
        if (announcement == null) {
            return false;
        }
        boolean levelRestricted = StringUtils.hasText(announcement.targetLevelCodes());
        boolean userRestricted = StringUtils.hasText(announcement.targetUserIds());
        if (!levelRestricted && !userRestricted) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        if (levelRestricted && matchesLevel(announcement.targetLevelCodes(), levelCode)) {
            return true;
        }
        return userRestricted && matchesUser(announcement.targetUserIds(), userId);
    }

    /**
     * 规范化定向字段：去空白、去重后逗号拼接。
     */
    public String normalizeCodes(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        Set<String> codes = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        return codes.isEmpty() ? null : String.join(",", codes);
    }

    /**
     * 规范化用户 ID 列表。
     */
    public String normalizeUserIds(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        Set<String> ids = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        return ids.isEmpty() ? null : String.join(",", ids);
    }

    /**
     * 执行matches等级。
     *
     * @param targetLevelCodes 目标等级Codes
     * @param levelCode 等级编码
     * @return 执行结果
     */
    private boolean matchesLevel(String targetLevelCodes, String levelCode) {
        String effectiveLevel = StringUtils.hasText(levelCode) ? levelCode.trim() : "FREE";
        return parseCsv(targetLevelCodes).stream()
                .anyMatch(code -> code.equalsIgnoreCase(effectiveLevel));
    }

    /**
     * 执行matches用户。
     *
     * @param targetUserIds 目标用户Ids
     * @param userId 用户 ID
     * @return 执行结果
     */
    private boolean matchesUser(String targetUserIds, Long userId) {
        return parseCsv(targetUserIds).contains(String.valueOf(userId));
    }

    /**
     * 执行parseCsv。
     *
     * @param raw raw
     * @return 执行结果
     */
    private Set<String> parseCsv(String raw) {
        if (!StringUtils.hasText(raw)) {
            return Collections.emptySet();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }
}
