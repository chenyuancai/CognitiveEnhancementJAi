package cn.cyc.ai.cog.platform.iam.service;

import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.dto.UserRegisterRequest;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.system.service.SecurityConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IamAuthServiceTest {

    @Mock
    private IamUserRepository iamUserRepository;
    @Mock
    private SecurityConfigService securityConfigService;
    @Mock
    private ObjectProvider<cn.cyc.ai.cog.common.spi.AccountProvisioner> accountProvisioner;

    @InjectMocks
    private IamAuthService iamAuthService;

    @Test
    void shouldRejectPhoneRegisterWhenDisabled() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setMode("PHONE");
        request.setPhone("13800000000");
        request.setPassword("secret1");

        when(securityConfigService.getBoolean(eq("auth.register.phone"), eq(false))).thenReturn(false);

        ServiceException ex = assertThrows(ServiceException.class, () -> iamAuthService.register(request));
        assertEquals("A0403", ex.getCode());
    }

    @Test
    void shouldRegisterByUsernameWhenEnabled() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setMode("USERNAME");
        request.setUsername("alice");
        request.setPassword("secret1");

        when(securityConfigService.getBoolean(eq("auth.register.username"), eq(true))).thenReturn(true);
        when(iamUserRepository.existsUsername("alice")).thenReturn(false);
        when(iamUserRepository.registerCustomer(any(), eq("alice"), any(), any(), any(), any()))
                .thenReturn(new IamUser(9L, 1L, "alice", "Alice", null, "ENABLED", null,
                        null, null, null, null, "CUSTOMER", LocalDateTime.now()));
        when(accountProvisioner.getIfAvailable()).thenReturn(null);

        IamUser user = iamAuthService.register(request);
        assertEquals("alice", user.username());
    }

    @Test
    void shouldRegisterByPhoneWhenEnabled() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setMode("PHONE");
        request.setPhone("13800000001");
        request.setPassword("secret1");

        when(securityConfigService.getBoolean(eq("auth.register.phone"), eq(false))).thenReturn(true);
        when(iamUserRepository.existsPhone("13800000001")).thenReturn(false);
        when(iamUserRepository.registerCustomer(any(), eq("p_13800000001"), any(), any(), any(), eq("13800000001")))
                .thenReturn(new IamUser(10L, 1L, "p_13800000001", "PhoneUser", null, "ENABLED", null,
                        null, "13800000001", null, null, "CUSTOMER", LocalDateTime.now()));
        when(accountProvisioner.getIfAvailable()).thenReturn(null);

        IamUser user = iamAuthService.register(request);
        assertEquals("p_13800000001", user.username());
    }

    @Test
    void shouldRegisterByEmailWhenEnabled() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setMode("EMAIL");
        request.setEmail("user@example.com");
        request.setPassword("secret1");

        when(securityConfigService.getBoolean(eq("auth.register.email"), eq(false))).thenReturn(true);
        when(iamUserRepository.existsEmail("user@example.com")).thenReturn(false);
        when(iamUserRepository.registerCustomer(any(), eq("user@example.com"), any(), any(), eq("user@example.com"), any()))
                .thenReturn(new IamUser(11L, 1L, "user@example.com", "MailUser", null, "ENABLED", null,
                        "user@example.com", null, null, null, "CUSTOMER", LocalDateTime.now()));
        when(accountProvisioner.getIfAvailable()).thenReturn(null);

        IamUser user = iamAuthService.register(request);
        assertEquals("user@example.com", user.username());
    }
}
