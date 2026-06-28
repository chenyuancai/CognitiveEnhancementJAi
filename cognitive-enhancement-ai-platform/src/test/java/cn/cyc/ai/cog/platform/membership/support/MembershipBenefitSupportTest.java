package cn.cyc.ai.cog.platform.membership.support;

import cn.cyc.ai.cog.platform.membership.repository.LevelBenefitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipBenefitSupportTest {

    @Mock
    private LevelBenefitRepository levelBenefitRepository;

    @InjectMocks
    private MembershipBenefitSupport support;

    @Test
    void shouldDetectEnabledBenefitFromJson() {
        String json = "{\"ai.scoring\":true,\"ai.tutoring\":false}";
        assertTrue(support.hasBenefit(json, "ai.scoring"));
        assertFalse(support.hasBenefit(json, "ai.tutoring"));
    }

    @Test
    void shouldPreferCatalogOverJson() {
        when(levelBenefitRepository.findBenefitValue(eq(2L), eq("ai.scoring")))
                .thenReturn(Optional.of("true"));
        assertTrue(support.hasBenefit(2L, "{\"ai.scoring\":false}", "ai.scoring"));
    }

    @Test
    void shouldResolveMonthlyTokenFromCatalog() {
        when(levelBenefitRepository.findBenefitValue(eq(1L), eq(MembershipBenefitSupport.BENEFIT_MONTHLY_TOKEN)))
                .thenReturn(Optional.of("200000"));
        assertEquals(200_000L, support.resolveMonthlyTokenQuota(1L, "{\"monthlyTokenK\":100}"));
    }

    @Test
    void shouldResolveMonthlyTokenFromJsonWhenCatalogMissing() {
        when(levelBenefitRepository.findBenefitValue(eq(1L), eq(MembershipBenefitSupport.BENEFIT_MONTHLY_TOKEN)))
                .thenReturn(Optional.empty());
        assertEquals(100_000L, support.resolveMonthlyTokenQuota(1L, "{\"monthlyTokenK\":100}"));
    }

    @Test
    void shouldFallbackDefaultMonthlyTokenQuota() {
        assertEquals(MembershipBenefitSupport.DEFAULT_MONTHLY_TOKEN_QUOTA,
                support.resolveMonthlyTokenQuota(null, null));
    }
}
