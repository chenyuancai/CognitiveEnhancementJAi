package cn.cyc.ai.cog.platform.operations.support;

import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnouncementAudienceSupportTest {

    private AnnouncementAudienceSupport support;

    @BeforeEach
    void setUp() {
        support = new AnnouncementAudienceSupport();
    }

    @Test
    void shouldAllowAllWhenNoTargeting() {
        Announcement announcement = announcement(null, null);
        assertTrue(support.isVisible(announcement, null, null));
        assertTrue(support.isVisible(announcement, 1L, "FREE"));
    }

    @Test
    void shouldMatchMemberLevel() {
        Announcement announcement = announcement("PRO", null);
        assertFalse(support.isVisible(announcement, 1L, "FREE"));
        assertTrue(support.isVisible(announcement, 2L, "PRO"));
    }

    @Test
    void shouldMatchUserGroup() {
        Announcement announcement = announcement(null, "2,3");
        assertFalse(support.isVisible(announcement, 1L, "FREE"));
        assertTrue(support.isVisible(announcement, 2L, "FREE"));
    }

    @Test
    void shouldMatchEitherLevelOrUserGroup() {
        Announcement announcement = announcement("PRO", "5");
        assertTrue(support.isVisible(announcement, 5L, "FREE"));
        assertTrue(support.isVisible(announcement, 2L, "PRO"));
        assertFalse(support.isVisible(announcement, 1L, "FREE"));
    }

    @Test
    void shouldNormalizeCodes() {
        assertTrue(support.normalizeCodes(" PRO , pro ,FREE ").contains("PRO"));
        assertTrue(support.normalizeCodes(" PRO , pro ,FREE ").contains("FREE"));
    }

    private Announcement announcement(String levelCodes, String userIds) {
        return new Announcement(1L, "title", "body", "PUBLISHED", LocalDateTime.now(), levelCodes, userIds);
    }
}
