package cn.cyc.ai.cog.platform.iam.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.spi.AccountProvisioner;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.dto.UserRegisterRequest;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.iam.support.AuthConfigKeys;
import cn.cyc.ai.cog.platform.system.service.SecurityConfigService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * IAM 认证服务：多方式注册（受安全配置开关控制）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class IamAuthService {

    /** 角色用户。 */
    private static final String ROLE_USER = "USER";

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 安全配置服务 */
    private final SecurityConfigService securityConfigService;

    /** 账户开户 SPI */
    private final ObjectProvider<AccountProvisioner> accountProvisioner;

    /** 密码编码器 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * @param iamUserRepository      IAM 用户仓储
     * @param securityConfigService  安全配置服务
     * @param accountProvisioner     账户开户 SPI
     */
    public IamAuthService(IamUserRepository iamUserRepository,
                          SecurityConfigService securityConfigService,
                          ObjectProvider<AccountProvisioner> accountProvisioner) {
        this.iamUserRepository = iamUserRepository;
        this.securityConfigService = securityConfigService;
        this.accountProvisioner = accountProvisioner;
    }

    /**
     * C 端用户注册。
     *
     * @param request 注册请求
     * @return 新注册用户
     */
    @Transactional
    public IamUser register(UserRegisterRequest request) {
        String mode = request.getMode() == null ? "" : request.getMode().trim().toUpperCase();
        return switch (mode) {
            case "USERNAME" -> registerByUsername(request);
            case "PHONE" -> registerByPhone(request);
            case "EMAIL" -> registerByEmail(request);
            default -> throw Errors.of(PlatformErrorCode.REGISTER_MODE_UNSUPPORTED, "不支持的注册方式：" + request.getMode());
        };
    }

    /**
     * 执行register人Username。
     *
     * @param request 请求
     * @return 执行结果
     */
    private IamUser registerByUsername(UserRegisterRequest request) {
        assertRegisterEnabled(AuthConfigKeys.REGISTER_USERNAME, true, "用户名密码注册");
        if (!StringUtils.hasText(request.getUsername())) {
            throw Errors.of(PlatformErrorCode.USERNAME_REQUIRED);
        }
        if (iamUserRepository.existsUsername(request.getUsername())) {
            throw Errors.of(PlatformErrorCode.USERNAME_EXISTS);
        }
        return completeRegister(request.getUsername(), request.getPassword(), request.getNickname(), null, null);
    }

    /**
     * 执行register人手机号。
     *
     * @param request 请求
     * @return 执行结果
     */
    private IamUser registerByPhone(UserRegisterRequest request) {
        assertRegisterEnabled(AuthConfigKeys.REGISTER_PHONE, false, "手机号注册");
        if (!StringUtils.hasText(request.getPhone())) {
            throw Errors.of(PlatformErrorCode.PHONE_REQUIRED);
        }
        if (iamUserRepository.existsPhone(request.getPhone())) {
            throw Errors.of(PlatformErrorCode.PHONE_EXISTS);
        }
        String username = "p_" + request.getPhone().trim();
        return completeRegister(username, request.getPassword(), request.getNickname(), null, request.getPhone());
    }

    /**
     * 执行register人邮箱。
     *
     * @param request 请求
     * @return 执行结果
     */
    private IamUser registerByEmail(UserRegisterRequest request) {
        assertRegisterEnabled(AuthConfigKeys.REGISTER_EMAIL, false, "邮箱注册");
        if (!StringUtils.hasText(request.getEmail())) {
            throw Errors.of(PlatformErrorCode.EMAIL_REQUIRED);
        }
        if (iamUserRepository.existsEmail(request.getEmail())) {
            throw Errors.of(PlatformErrorCode.EMAIL_EXISTS);
        }
        String username = request.getEmail().trim();
        return completeRegister(username, request.getPassword(), request.getNickname(), request.getEmail(), null);
    }

    /**
     * 执行assertRegister是否启用。
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @param label label
     */
    private void assertRegisterEnabled(String configKey, boolean defaultValue, String label) {
        if (!securityConfigService.getBoolean(configKey, defaultValue)) {
            throw Errors.of(PlatformErrorCode.REGISTER_CHANNEL_CLOSED, label + "未开放");
        }
    }

    /**
     * 执行completeRegister。
     * @return 执行结果
     */
    private IamUser completeRegister(String username, String password, String nickname,
                                     String email, String phone) {
        if (!StringUtils.hasText(password) || password.length() < 6) {
            throw Errors.of(PlatformErrorCode.PASSWORD_TOO_SHORT);
        }
        IamUser user = iamUserRepository.registerCustomer(
                1L, username, passwordEncoder.encode(password), nickname, email, phone);
        iamUserRepository.assignRoleByCode(user.id(), ROLE_USER);
        AccountProvisioner provisioner = accountProvisioner.getIfAvailable();
        if (provisioner != null) {
            provisioner.provisionIndividual(user.id(), user.nickname());
        }
        return user;
    }
}
