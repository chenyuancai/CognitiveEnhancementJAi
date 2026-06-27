package cn.cyc.ai.cog.common.exception;

import cn.cyc.ai.cog.api.enums.ErrorCode;

/**
 * 平台业务错误码：映射 HTTP 级 {@link ErrorCode} + 默认中文提示。
 * <p>
 * 抛出时使用 {@link Errors#throwError(PlatformErrorCode)}。
 */
public enum PlatformErrorCode implements IResultCode {

    // —— 通用 ——
    UNAUTHORIZED(ErrorCode.UNAUTHORIZED, "未登录或令牌无效"),
    NOT_LOGGED_IN(ErrorCode.UNAUTHORIZED, "未登录"),
    FORBIDDEN(ErrorCode.FORBIDDEN, "无权访问当前资源"),
    NOT_FOUND(ErrorCode.NOT_FOUND, "资源不存在"),
    BAD_REQUEST(ErrorCode.BAD_REQUEST, "请求参数错误"),
    CONFLICT(ErrorCode.CONFLICT, "资源状态冲突"),
    TOO_MANY_REQUESTS(ErrorCode.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试"),
    SERVICE_UNAVAILABLE(ErrorCode.SERVICE_UNAVAILABLE, "服务暂不可用"),
    BUSINESS_ERROR(ErrorCode.BUSINESS_ERROR, "业务处理失败"),

    // —— IAM / 账户 ——
    USER_NOT_FOUND(ErrorCode.NOT_FOUND, "用户不存在"),
    USER_CANNOT_MODIFY_SELF(ErrorCode.FORBIDDEN, "不能修改当前登录用户状态"),
    USER_STATUS_INVALID(ErrorCode.BAD_REQUEST, "非法状态"),
    USER_STATUS_UNAVAILABLE(ErrorCode.FORBIDDEN, "用户状态不可用"),
    USER_ACCOUNT_NOT_BOUND(ErrorCode.NOT_FOUND, "用户未绑定账户"),
    ROLE_NOT_FOUND(ErrorCode.NOT_FOUND, "角色不存在"),
    ROLE_CODE_REQUIRED(ErrorCode.BAD_REQUEST, "角色编码不能为空"),
    ROLE_CODE_EXISTS(ErrorCode.CONFLICT, "角色编码已存在"),
    BUILTIN_ROLE_NOT_DELETABLE(ErrorCode.FORBIDDEN, "内置角色不可删除"),
    PERMISSION_NOT_FOUND(ErrorCode.NOT_FOUND, "权限点不存在"),
    PERMISSION_CODE_REQUIRED(ErrorCode.BAD_REQUEST, "权限码不能为空"),
    PERMISSION_CODE_EXISTS(ErrorCode.CONFLICT, "权限码已存在"),
    BUILTIN_PERMISSION_NOT_EDITABLE(ErrorCode.FORBIDDEN, "系统内置权限不可编辑"),
    BUILTIN_PERMISSION_NOT_DELETABLE(ErrorCode.FORBIDDEN, "系统内置权限不可删除"),
    PERMISSION_DENIED(ErrorCode.FORBIDDEN, "无权访问"),
    TENANT_NOT_FOUND(ErrorCode.NOT_FOUND, "租户不存在"),
    TENANT_CODE_EXISTS(ErrorCode.CONFLICT, "租户编码已存在"),
    TENANT_STATUS_INVALID(ErrorCode.BAD_REQUEST, "非法状态"),
    DEFAULT_TENANT_CODE_IMMUTABLE(ErrorCode.CONFLICT, "平台默认租户编码不可修改"),
    DEFAULT_TENANT_NOT_DISABLE(ErrorCode.CONFLICT, "平台默认租户不可停用"),
    REGISTER_MODE_UNSUPPORTED(ErrorCode.BAD_REQUEST, "不支持的注册方式"),
    USERNAME_REQUIRED(ErrorCode.BAD_REQUEST, "用户名不能为空"),
    USERNAME_EXISTS(ErrorCode.CONFLICT, "用户名已存在"),
    PHONE_REQUIRED(ErrorCode.BAD_REQUEST, "手机号不能为空"),
    PHONE_EXISTS(ErrorCode.CONFLICT, "手机号已注册"),
    EMAIL_REQUIRED(ErrorCode.BAD_REQUEST, "邮箱不能为空"),
    EMAIL_EXISTS(ErrorCode.CONFLICT, "邮箱已注册"),
    REGISTER_CHANNEL_CLOSED(ErrorCode.FORBIDDEN, "注册渠道未开放"),
    PASSWORD_TOO_SHORT(ErrorCode.BAD_REQUEST, "密码长度不能少于 6 位"),

    // —— 组织 ——
    ORG_NOT_FOUND(ErrorCode.NOT_FOUND, "组织不存在"),
    ORG_MEMBER_NOT_FOUND(ErrorCode.NOT_FOUND, "组织成员不存在"),
    ORG_OWNER_NOT_REMOVABLE(ErrorCode.FORBIDDEN, "不能移除组织所有者"),
    ORG_DEPARTMENT_NOT_FOUND(ErrorCode.NOT_FOUND, "部门不存在"),
    ORG_SEAT_FULL(ErrorCode.CONFLICT, "组织席位已满"),
    ORG_MEMBER_EXISTS(ErrorCode.CONFLICT, "用户已在组织中"),

    // —— 账户 / 额度 ——
    ACCOUNT_NOT_FOUND(ErrorCode.NOT_FOUND, "账户不存在"),
    QUOTA_ACCOUNT_NOT_FOUND(ErrorCode.NOT_FOUND, "额度账户不存在"),
    QUOTA_INSUFFICIENT(ErrorCode.CONFLICT, "额度不足"),
    QUOTA_ALLOC_BELOW_USED(ErrorCode.CONFLICT, "分配额度不能小于已用额度"),
    QUOTA_MEMBER_ALLOC_INSUFFICIENT(ErrorCode.CONFLICT, "成员分配额度不足"),
    QUOTA_OPEN_AMOUNT_INVALID(ErrorCode.BAD_REQUEST, "开户额度必须大于 0"),
    QUOTA_DEDUCT_AMOUNT_INVALID(ErrorCode.BAD_REQUEST, "扣减额度必须大于 0"),
    QUOTA_DEDUCT_CONFLICT(ErrorCode.CONFLICT, "额度扣减冲突，请重试"),
    QUOTA_BUCKET_UNKNOWN(ErrorCode.BAD_REQUEST, "未知额度桶"),

    // —— 计费 / 支付 ——
    ORDER_NOT_FOUND(ErrorCode.NOT_FOUND, "订单不存在"),
    ORDER_PENDING_ONLY_PAY(ErrorCode.CONFLICT, "仅待支付订单可支付"),
    ORDER_PENDING_ONLY_CANCEL(ErrorCode.CONFLICT, "仅待支付订单可取消"),
    ORDER_PENDING_ONLY_MARK_PAID(ErrorCode.CONFLICT, "仅待支付订单可标记为已支付"),
    ORDER_PENDING_ONLY_PREPAY(ErrorCode.CONFLICT, "仅待支付订单可发起支付"),
    ORDER_REFUND_STATUS_INVALID(ErrorCode.CONFLICT, "仅已支付/已发放订单可退款"),
    ORDER_REFUND_AMOUNT_EXCEEDED(ErrorCode.BAD_REQUEST, "退款金额不能超过订单金额"),
    ORDER_PAID_ONLY_FULFILL(ErrorCode.CONFLICT, "仅已支付订单可发放"),
    ORDER_STATUS_NOT_PAYABLE(ErrorCode.CONFLICT, "订单状态不可支付"),
    ORDER_CALLBACK_AMOUNT_MISMATCH(ErrorCode.BAD_REQUEST, "回调金额与订单不一致"),
    ORDER_TYPE_UNKNOWN(ErrorCode.BAD_REQUEST, "未知订单类型"),
    ORDER_TYPE_INVALID(ErrorCode.BAD_REQUEST, "非法订单类型"),
    PAYMENT_CHANNEL_UNSUPPORTED(ErrorCode.BAD_REQUEST, "不支持的支付通道"),
    PAYMENT_CHANNEL_EMPTY(ErrorCode.BAD_REQUEST, "支付通道不能为空"),
    PAYMENT_WECHAT_NOT_CONFIGURED(ErrorCode.BAD_REQUEST, "微信通道未配置 appId/mchId/apiV3Key"),
    PAYMENT_ALIPAY_NOT_CONFIGURED(ErrorCode.BAD_REQUEST, "支付宝通道未配置 appId/merchantPrivateKey"),
    PAYMENT_MOCK_VERIFY_FAILED(ErrorCode.FORBIDDEN, "MOCK 回调验签失败"),
    PAYMENT_WECHAT_PUBKEY_MISSING(ErrorCode.FORBIDDEN, "微信回调未配置平台公钥"),
    PAYMENT_WECHAT_SIGNATURE_MISSING(ErrorCode.FORBIDDEN, "微信回调缺少签名"),
    PAYMENT_WECHAT_TIMESTAMP_MISSING(ErrorCode.FORBIDDEN, "微信回调缺少 timestamp/nonce"),
    PAYMENT_ALIPAY_PUBKEY_MISSING(ErrorCode.FORBIDDEN, "支付宝回调未配置公钥"),
    PAYMENT_ALIPAY_SIGNATURE_MISSING(ErrorCode.FORBIDDEN, "支付宝回调缺少签名"),
    PAYMENT_HMAC_ERROR(ErrorCode.FORBIDDEN, "HMAC 签名异常"),
    PAYMENT_RSA_SIGN_ERROR(ErrorCode.FORBIDDEN, "RSA 签名异常"),
    PAYMENT_RSA_VERIFY_FAILED(ErrorCode.FORBIDDEN, "RSA 验签失败"),
    PAYMENT_RSA_VERIFY_ERROR(ErrorCode.FORBIDDEN, "RSA 验签异常"),
    SUBSCRIPTION_PACKAGE_NOT_FOUND(ErrorCode.NOT_FOUND, "订阅套餐不存在"),
    QUOTA_PACKAGE_NOT_FOUND(ErrorCode.NOT_FOUND, "额度包不存在"),

    // —— 会员 ——
    MEMBERSHIP_NOT_FOUND(ErrorCode.NOT_FOUND, "会员记录不存在"),
    MEMBERSHIP_LEVEL_NOT_FOUND(ErrorCode.NOT_FOUND, "会员等级不存在"),
    MEMBERSHIP_LEVEL_DEFAULT_NOT_FOUND(ErrorCode.NOT_FOUND, "未配置默认会员等级"),
    MEMBERSHIP_LEVEL_CODE_EXISTS(ErrorCode.CONFLICT, "等级编码已存在"),

    // —— 知识 / 内容 ——
    CONTENT_NOT_FOUND(ErrorCode.NOT_FOUND, "内容不存在"),
    CONTENT_VERSION_NOT_FOUND(ErrorCode.NOT_FOUND, "内容版本不存在"),
    CONTENT_NOT_PUBLISHED(ErrorCode.NOT_FOUND, "内容不存在或未发布"),
    CONTENT_TAG_NOT_FOUND(ErrorCode.NOT_FOUND, "标签不存在"),
    CONTENT_AUDIT_STATUS_INVALID(ErrorCode.CONFLICT, "仅待审核内容可审核"),
    CONTENT_OFFLINE_STATUS_INVALID(ErrorCode.CONFLICT, "仅已发布内容可下线"),
    KNOWLEDGE_PACKAGE_NOT_FOUND(ErrorCode.NOT_FOUND, "知识包不存在"),
    KNOWLEDGE_PACKAGE_NOT_ENABLED(ErrorCode.NOT_FOUND, "知识包未启用"),
    KNOWLEDGE_PACKAGE_ITEM_NOT_FOUND(ErrorCode.NOT_FOUND, "知识包条目不存在"),
    CONTENT_IMPORT_JOB_NOT_FOUND(ErrorCode.NOT_FOUND, "导入任务不存在"),
    CONTENT_IMPORT_SOURCE_REQUIRED(ErrorCode.BAD_REQUEST, "请提供 fileContent、fileUrl 或 fileId"),
    CONTENT_IMPORT_FILENAME_EMPTY(ErrorCode.BAD_REQUEST, "导入文件名为空"),
    CONTENT_IMPORT_CSV_EMPTY(ErrorCode.BAD_REQUEST, "导入 CSV 内容为空"),

    // —— 运营 ——
    BANNER_NOT_FOUND(ErrorCode.NOT_FOUND, "Banner 不存在"),
    ANNOUNCEMENT_NOT_FOUND(ErrorCode.NOT_FOUND, "公告不存在"),
    MESSAGE_TEMPLATE_NOT_FOUND(ErrorCode.NOT_FOUND, "消息模板不存在"),
    MESSAGE_TEMPLATE_CODE_EXISTS(ErrorCode.CONFLICT, "模板编码已存在"),
    MESSAGE_TEMPLATE_VARIABLE_MISSING(ErrorCode.BAD_REQUEST, "缺少模板变量"),
    MESSAGE_TEMPLATE_SCHEMA_INVALID(ErrorCode.BAD_REQUEST, "variable_schema 格式无效"),
    IN_APP_MESSAGE_NOT_FOUND(ErrorCode.NOT_FOUND, "站内信不存在"),
    SUPPORT_TICKET_NOT_FOUND(ErrorCode.NOT_FOUND, "工单不存在"),
    SUPPORT_TICKET_STATUS_INVALID(ErrorCode.BAD_REQUEST, "工单状态无效"),
    SUPPORT_TICKET_FORBIDDEN(ErrorCode.FORBIDDEN, "无权查看该工单"),
    ORDER_ACCESS_FORBIDDEN(ErrorCode.FORBIDDEN, "无权访问该订单"),

    // —— 系统 ——
    FEATURE_SWITCH_NOT_FOUND(ErrorCode.NOT_FOUND, "Feature 开关不存在"),
    SECURITY_CONFIG_NOT_FOUND(ErrorCode.NOT_FOUND, "安全配置不存在"),
    DICT_TYPE_NOT_FOUND(ErrorCode.NOT_FOUND, "字典类型不存在"),
    DICT_TYPE_CODE_DUPLICATE(ErrorCode.CONFLICT, "字典类型编码已存在"),
    DICT_ITEM_NOT_FOUND(ErrorCode.NOT_FOUND, "字典项不存在"),
    DICT_ITEM_VALUE_DUPLICATE(ErrorCode.CONFLICT, "字典项值已存在"),
    ENUM_VALUE_NOT_INTEGER(ErrorCode.BAD_REQUEST, "枚举项值必须为整数"),

    // —— Center / Runtime 元数据与执行 ——
    METADATA_NOT_FOUND(ErrorCode.NOT_FOUND, "元数据不存在"),
    METADATA_ALREADY_EXISTS(ErrorCode.CONFLICT, "元数据已存在"),
    METADATA_VERSION_EXISTS(ErrorCode.CONFLICT, "版本已存在"),
    METADATA_BASELINE_NOT_FOUND(ErrorCode.NOT_FOUND, "基线版本不存在"),
    PROMPT_NOT_FOUND(ErrorCode.NOT_FOUND, "Prompt 不存在"),
    RUNTIME_CAPABILITY_NOT_FOUND(ErrorCode.NOT_FOUND, "能力不存在"),
    RUNTIME_CAPABILITY_DISABLED(ErrorCode.CONFLICT, "能力未启用"),
    RUNTIME_AGENT_NOT_FOUND(ErrorCode.NOT_FOUND, "Agent 不存在"),
    RUNTIME_AGENT_DISABLED(ErrorCode.CONFLICT, "Agent 未启用"),
    RUNTIME_TOOL_NOT_FOUND(ErrorCode.NOT_FOUND, "Tool 不存在"),
    RUNTIME_TOOL_DISABLED(ErrorCode.CONFLICT, "Tool 未启用"),
    RUNTIME_SKILL_NOT_FOUND(ErrorCode.NOT_FOUND, "Skill 不存在"),
    RUNTIME_SKILL_DISABLED(ErrorCode.CONFLICT, "Skill 未启用"),
    RUNTIME_MODEL_NOT_FOUND(ErrorCode.NOT_FOUND, "模型不存在"),
    RUNTIME_MODEL_DISABLED(ErrorCode.CONFLICT, "模型未启用"),
    RUNTIME_MODEL_UNAVAILABLE(ErrorCode.CONFLICT, "模型不可用且无降级模型"),
    RUNTIME_SESSION_NOT_FOUND(ErrorCode.NOT_FOUND, "会话不存在"),
    RUNTIME_EXECUTION_NOT_FOUND(ErrorCode.NOT_FOUND, "执行记录不存在"),
    RUNTIME_TRACE_NOT_FOUND(ErrorCode.NOT_FOUND, "Trace 不存在"),
    RUNTIME_FILE_NOT_FOUND(ErrorCode.NOT_FOUND, "文件记录不存在"),
    RUNTIME_TOOL_CONFIRM_REQUIRED(ErrorCode.CONFLICT, "高风险 Tool 调试需确认"),
    RUNTIME_POLICY_DENIED(ErrorCode.FORBIDDEN, "策略检查未通过"),
    RUNTIME_QUOTA_EXCEEDED(ErrorCode.TOO_MANY_REQUESTS, "额度不足"),
    RUNTIME_LLM_CREDENTIAL_MISSING(ErrorCode.BAD_REQUEST, "模型凭证未配置"),
    RUNTIME_PAGE_INVALID(ErrorCode.BAD_REQUEST, "page 必须大于等于 1"),
    RUNTIME_PAGE_SIZE_INVALID(ErrorCode.BAD_REQUEST, "size 必须在 1 到 100 之间"),
    RUNTIME_SORT_FIELD_INVALID(ErrorCode.BAD_REQUEST, "不支持的排序字段"),
    RUNTIME_SORT_DIRECTION_INVALID(ErrorCode.BAD_REQUEST, "排序方向必须是 asc 或 desc");

    private final ErrorCode category;
    private final String message;

    PlatformErrorCode(ErrorCode category, String message) {
        this.category = category;
        this.message = message;
    }

    @Override
    public String getCode() {
        return category.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }

    /** HTTP 级错误分类，供全局异常处理器映射状态码。 */
    public ErrorCode getCategory() {
        return category;
    }
}
