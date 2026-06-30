package cn.cyc.ai.cog.platform.iam.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.dto.UserPageQuery;

/**
 * IAM 用户仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface IamUserRepository {

    /**
     * 分页查询用户。
     *
     * @param query 分页与筛选条件
     * @return 用户分页结果
     */
    PageResult<IamUser> page(UserPageQuery query);

    /**
     * 按 ID 查询用户，不存在时抛出业务异常。
     *
     * @param id 用户 ID
     * @return 用户领域对象
     */
    IamUser requireById(Long id);

    /**
     * 更新用户状态、封禁原因与截止时间。
     *
     * @param id        用户 ID
     * @param status    目标状态
     * @param banReason 封禁原因，可为 null
     * @param banUntil  封禁截止时间，可为 null
     * @return 更新后的用户
     */
    IamUser updateStatus(Long id, String status, String banReason, java.time.LocalDateTime banUntil);

    /**
     * 若封禁已到期则自动恢复为 ENABLED。
     *
     * @param id 用户 ID
     * @return 恢复后的用户；未封禁或尚未到期则原样返回
     */
    IamUser resolveBanIfExpired(Long id);

    /**
     * 按用户名查询用户，不存在返回 null。
     *
     * @param username 用户名
     * @return 用户或 null
     */
    IamUser findByUsername(String username);

    /**
     * 注册 C 端用户。
     *
     * @param tenantId     租户 ID
     * @param username     登录名
     * @param passwordHash 密码哈希
     * @param nickname     昵称
     * @param email        邮箱，可为 null
     * @param phone        手机号，可为 null
     * @return 新用户
     */
    IamUser registerCustomer(Long tenantId, String username, String passwordHash,
                             String nickname, String email, String phone);

    /**
     * 为用户分配角色（按角色编码）。
     *
     * @param userId   用户 ID
     * @param roleCode 角色编码
     */
    void assignRoleByCode(Long userId, String roleCode);

    /**
     * 用户名是否已存在。
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsUsername(String username);

    /**
     * 手机号是否已存在。
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsPhone(String phone);

    /**
     * 邮箱是否已存在。
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsEmail(String email);

    /**
     * 绑定用户主账户（仅当尚未绑定时写入）。
     *
     * @param userId    用户 ID
     * @param accountId 账户 ID
     */
    void bindPrimaryAccountIfAbsent(Long userId, Long accountId);

    /**
     * 更新用户租户与主账户（组织开户场景）。
     *
     * @param userId    用户 ID
     * @param tenantId  租户 ID
     * @param accountId 主账户 ID
     */
    void updateTenantAndPrimaryAccount(Long userId, Long tenantId, Long accountId);

    /**
     * 统计用户数量（按租户与时间范围可选筛选）。
     *
     * @param tenantId 租户 ID，可为 null
     * @param start    创建时间下限，可为 null
     * @param end      创建时间上限，可为 null
     * @return 用户数量
     */
    long countUsers(Long tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    /**
     * 查询时间范围内创建的用户列表。
     *
     * @param tenantId 租户 ID，可为 null
     * @param start    创建时间下限
     * @param end      创建时间上限
     * @return 用户列表
     */
    java.util.List<IamUser> listUsersCreatedBetween(Long tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    /**
     * 统计近 N 日有登录记录的用户数（按 last_login_time）。
     *
     * @param tenantId 租户 ID，可为 null
     * @param days     回溯天数（含今日）
     * @return 活跃用户数
     */
    long countActiveUsers(Long tenantId, int days);

    /**
     * 查询用户绑定的角色编码列表。
     *
     * @param userId 用户 ID
     * @return 角色编码列表
     */
    java.util.List<String> listRoleCodes(Long userId);

    /**
     * 批量恢复封禁已到期的用户为 ENABLED。
     *
     * @return 恢复的用户数量
     */
    int recoverExpiredBans();
}
