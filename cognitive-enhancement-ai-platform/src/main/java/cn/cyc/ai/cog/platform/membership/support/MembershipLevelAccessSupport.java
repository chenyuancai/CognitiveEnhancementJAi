package cn.cyc.ai.cog.platform.membership.support;

import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员等级可见性比较（基于 sortNo）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class MembershipLevelAccessSupport {

    /**
     * 构建等级编码 → 排序权重映射。
     *
     * @param levels 等级列表
     * @return 排序映射
     */
    public Map<String, Integer> buildSortIndex(List<MembershipLevel> levels) {
        Map<String, Integer> index = new LinkedHashMap<>();
        for (MembershipLevel level : levels) {
            index.put(level.levelCode(), level.sortNo() == null ? 0 : level.sortNo());
        }
        return index;
    }

    /**
     * 判断用户等级是否满足内容最低等级要求。
     *
     * @param userLevelCode    用户当前等级编码
     * @param requiredMinLevel 内容要求的最低等级
     * @param sortIndex        等级排序映射
     * @return 是否可访问
     */
    public boolean canAccess(String userLevelCode, String requiredMinLevel, Map<String, Integer> sortIndex) {
        if (!StringUtils.hasText(requiredMinLevel) || "FREE".equalsIgnoreCase(requiredMinLevel)) {
            return true;
        }
        String effectiveUserLevel = StringUtils.hasText(userLevelCode) ? userLevelCode : "FREE";
        int userRank = sortIndex.getOrDefault(effectiveUserLevel, 0);
        int requiredRank = sortIndex.getOrDefault(requiredMinLevel, Integer.MAX_VALUE);
        return userRank >= requiredRank;
    }
}
