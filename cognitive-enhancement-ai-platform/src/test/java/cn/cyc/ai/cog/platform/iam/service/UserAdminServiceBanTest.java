package cn.cyc.ai.cog.platform.iam.service;

import cn.cyc.ai.cog.common.spi.UserSessionRevoker;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.api.enums.IamUserStatus;
import cn.cyc.ai.cog.platform.iam.dto.UserStatusUpdateRequest;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceBanTest {

    @Mock
    private IamUserRepository iamUserRepository;
    @Mock
    private AccountMembershipRepository accountMembershipRepository;
    @Mock
    private ObjectProvider<UserSessionRevoker> userSessionRevoker;
    @Mock
    private UserSessionRevoker revoker;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void shouldRevokeSessionsWhenBanningUser() {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setStatus(IamUserStatus.BANNED.code());
        request.setBanReason("违规");

        IamUser existing = new IamUser(2L, 1L, "bob", "Bob", null, "ENABLED", 1L,
                null, null, null, null, "CUSTOMER", LocalDateTime.now());
        IamUser banned = new IamUser(2L, 1L, "bob", "Bob", null, IamUserStatus.BANNED.code(), 1L,
                null, null, "违规", null, "CUSTOMER", LocalDateTime.now());

        when(iamUserRepository.requireById(2L)).thenReturn(existing);
        when(iamUserRepository.updateStatus(2L, IamUserStatus.BANNED.code(), "违规", null)).thenReturn(banned);
        doAnswer(invocation -> {
            invocation.getArgument(0, java.util.function.Consumer.class).accept(revoker);
            return null;
        }).when(userSessionRevoker).ifAvailable(any());
        when(accountMembershipRepository.findByAccountId(1L)).thenReturn(null);

        userAdminService.updateStatus(2L, request);

        verify(revoker).revokeByPrincipalName("bob");
    }
}
