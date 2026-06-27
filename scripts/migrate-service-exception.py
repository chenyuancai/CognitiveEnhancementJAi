#!/usr/bin/env python3
"""批量将 ServiceException 魔法字符串迁移为 PlatformErrorCode + Errors。"""
from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

# (旧 code, 旧 message 前缀或完整) -> PlatformErrorCode
EXACT = {
    ('FORBIDDEN', '不能修改当前登录用户状态'): 'USER_CANNOT_MODIFY_SELF',
    ('BAD_REQUEST', '非法状态'): 'USER_STATUS_INVALID',
    ('NOT_FOUND', '工单不存在'): 'SUPPORT_TICKET_NOT_FOUND',
    ('NOT_FOUND', '额度包不存在'): 'QUOTA_PACKAGE_NOT_FOUND',
    ('FORBIDDEN', '无权查看该工单'): 'SUPPORT_TICKET_FORBIDDEN',
    ('NOT_FOUND', 'Feature 开关不存在'): 'FEATURE_SWITCH_NOT_FOUND',
    ('NOT_FOUND', '标签不存在'): 'CONTENT_TAG_NOT_FOUND',
    ('NOT_FOUND', '导入任务不存在'): 'CONTENT_IMPORT_JOB_NOT_FOUND',
    ('NOT_FOUND', '组织成员不存在'): 'ORG_MEMBER_NOT_FOUND',
    ('FORBIDDEN', '不能移除组织所有者'): 'ORG_OWNER_NOT_REMOVABLE',
    ('CONFLICT', '仅待支付订单可支付'): 'ORDER_PENDING_ONLY_PAY',
    ('FORBIDDEN', '无权访问该订单'): 'ORDER_ACCESS_FORBIDDEN',
    ('UNAUTHORIZED', '未登录'): 'NOT_LOGGED_IN',
    ('NOT_FOUND', '部门不存在'): 'ORG_DEPARTMENT_NOT_FOUND',
    ('NOT_FOUND', '安全配置不存在'): 'SECURITY_CONFIG_NOT_FOUND',
    ('BAD_REQUEST', '请提供 fileContent 或 fileUrl'): 'CONTENT_IMPORT_SOURCE_REQUIRED',
    ('BAD_REQUEST', '导入文件名为空'): 'CONTENT_IMPORT_FILENAME_EMPTY',
    ('BAD_REQUEST', '导入 CSV 内容为空'): 'CONTENT_IMPORT_CSV_EMPTY',
    ('UNAUTHORIZED', '未登录或令牌无效'): 'UNAUTHORIZED',
    ('NOT_FOUND', '用户未绑定账户'): 'USER_ACCOUNT_NOT_BOUND',
    ('BAD_REQUEST', '用户名不能为空'): 'USERNAME_REQUIRED',
    ('CONFLICT', '用户名已存在'): 'USERNAME_EXISTS',
    ('BAD_REQUEST', '手机号不能为空'): 'PHONE_REQUIRED',
    ('CONFLICT', '手机号已注册'): 'PHONE_EXISTS',
    ('BAD_REQUEST', '邮箱不能为空'): 'EMAIL_REQUIRED',
    ('CONFLICT', '邮箱已注册'): 'EMAIL_EXISTS',
    ('BAD_REQUEST', '密码长度不能少于 6 位'): 'PASSWORD_TOO_SHORT',
    ('SERVICE_UNAVAILABLE', 'AI 运行时未装配'): 'SERVICE_UNAVAILABLE',
    ('TOO_MANY_REQUESTS', '请求过于频繁，请稍后再试'): 'TOO_MANY_REQUESTS',
    ('NOT_FOUND', '知识包条目不存在'): 'KNOWLEDGE_PACKAGE_ITEM_NOT_FOUND',
    ('NOT_FOUND', '知识包不存在'): 'KNOWLEDGE_PACKAGE_NOT_FOUND',
    ('NOT_FOUND', '站内信不存在'): 'IN_APP_MESSAGE_NOT_FOUND',
    ('NOT_FOUND', '消息模板不存在'): 'MESSAGE_TEMPLATE_NOT_FOUND',
    ('CONFLICT', '模板编码已存在'): 'MESSAGE_TEMPLATE_CODE_EXISTS',
    ('NOT_FOUND', '公告不存在'): 'ANNOUNCEMENT_NOT_FOUND',
    ('NOT_FOUND', '组织不存在'): 'ORG_NOT_FOUND',
    ('BAD_REQUEST', '权限码不能为空'): 'PERMISSION_CODE_REQUIRED',
    ('FORBIDDEN', '系统内置权限不可编辑'): 'BUILTIN_PERMISSION_NOT_EDITABLE',
    ('FORBIDDEN', '系统内置权限不可删除'): 'BUILTIN_PERMISSION_NOT_DELETABLE',
    ('CONFLICT', '分配额度不能小于已用额度'): 'QUOTA_ALLOC_BELOW_USED',
    ('CONFLICT', '仅已支付订单可发放'): 'ORDER_PAID_ONLY_FULFILL',
    ('NOT_FOUND', '内容不存在或未发布'): 'CONTENT_NOT_PUBLISHED',
    ('NOT_FOUND', '知识包未启用'): 'KNOWLEDGE_PACKAGE_NOT_ENABLED',
    ('NOT_FOUND', '用户不存在'): 'USER_NOT_FOUND',
    ('CONFLICT', '订单状态不可支付：'): 'ORDER_STATUS_NOT_PAYABLE',
    ('BAD_REQUEST', '回调金额与订单不一致'): 'ORDER_CALLBACK_AMOUNT_MISMATCH',
    ('BAD_REQUEST', '支付通道不能为空'): 'PAYMENT_CHANNEL_EMPTY',
    ('CONFLICT', '平台默认租户编码不可修改'): 'DEFAULT_TENANT_CODE_IMMUTABLE',
    ('CONFLICT', '平台默认租户不可停用'): 'DEFAULT_TENANT_NOT_DISABLE',
    ('NOT_FOUND', '租户不存在'): 'TENANT_NOT_FOUND',
    ('CONFLICT', '额度不足'): 'QUOTA_INSUFFICIENT',
    ('NOT_FOUND', '额度账户不存在'): 'QUOTA_ACCOUNT_NOT_FOUND',
    ('BAD_REQUEST', '开户额度必须大于 0'): 'QUOTA_OPEN_AMOUNT_INVALID',
    ('BAD_REQUEST', '扣减额度必须大于 0'): 'QUOTA_DEDUCT_AMOUNT_INVALID',
    ('CONFLICT', '额度扣减冲突，请重试'): 'QUOTA_DEDUCT_CONFLICT',
    ('BAD_REQUEST', '未知额度桶'): 'QUOTA_BUCKET_UNKNOWN',
    ('BAD_REQUEST', '非法订单类型'): 'ORDER_TYPE_INVALID',
    ('CONFLICT', '组织席位已满'): 'ORG_SEAT_FULL',
    ('CONFLICT', '用户已在组织中'): 'ORG_MEMBER_EXISTS',
    ('BAD_REQUEST', '角色编码不能为空'): 'ROLE_CODE_REQUIRED',
    ('FORBIDDEN', '内置角色不可删除'): 'BUILTIN_ROLE_NOT_DELETABLE',
    ('FORBIDDEN', 'MOCK 回调验签失败'): 'PAYMENT_MOCK_VERIFY_FAILED',
    ('FORBIDDEN', '微信回调未配置平台公钥'): 'PAYMENT_WECHAT_PUBKEY_MISSING',
    ('FORBIDDEN', '微信回调缺少签名'): 'PAYMENT_WECHAT_SIGNATURE_MISSING',
    ('FORBIDDEN', '微信回调缺少 timestamp/nonce'): 'PAYMENT_WECHAT_TIMESTAMP_MISSING',
    ('FORBIDDEN', '支付宝回调未配置公钥'): 'PAYMENT_ALIPAY_PUBKEY_MISSING',
    ('FORBIDDEN', '支付宝回调缺少签名'): 'PAYMENT_ALIPAY_SIGNATURE_MISSING',
    ('FORBIDDEN', 'HMAC 签名异常'): 'PAYMENT_HMAC_ERROR',
    ('FORBIDDEN', 'RSA 签名异常'): 'PAYMENT_RSA_SIGN_ERROR',
    ('FORBIDDEN', 'RSA 验签失败'): 'PAYMENT_RSA_VERIFY_FAILED',
    ('FORBIDDEN', 'RSA 验签异常'): 'PAYMENT_RSA_VERIFY_ERROR',
    ('BAD_REQUEST', '微信通道未配置 appId/mchId/apiV3Key'): 'PAYMENT_WECHAT_NOT_CONFIGURED',
    ('BAD_REQUEST', '支付宝通道未配置 appId/merchantPrivateKey'): 'PAYMENT_ALIPAY_NOT_CONFIGURED',
    ('BAD_REQUEST', 'variable_schema 格式无效'): 'MESSAGE_TEMPLATE_SCHEMA_INVALID',
    ('BAD_REQUEST', '缺少模板变量：'): 'MESSAGE_TEMPLATE_VARIABLE_MISSING',
}

PREFIX = {
    ('INVALID_ARGUMENT', '工单状态无效：'): 'SUPPORT_TICKET_STATUS_INVALID',
    ('NOT_FOUND', '角色不存在：'): 'ROLE_NOT_FOUND',
    ('NOT_FOUND', '账户不存在：'): 'ACCOUNT_NOT_FOUND',
    ('NOT_FOUND', '订单不存在：'): 'ORDER_NOT_FOUND',
    ('BAD_REQUEST', '不支持的支付通道：'): 'PAYMENT_CHANNEL_UNSUPPORTED',
    ('BAD_REQUEST', '不支持的注册方式：'): 'REGISTER_MODE_UNSUPPORTED',
    ('FORBIDDEN', None): 'FORBIDDEN',  # dynamic label + 未开放
    ('FORBIDDEN', '用户状态不可用：'): 'USER_STATUS_UNAVAILABLE',
    ('FORBIDDEN', None): None,
    ('BAD_REQUEST', '未知订单类型：'): 'ORDER_TYPE_UNKNOWN',
    ('CONFLICT', '仅待支付订单可取消，当前状态：'): 'ORDER_PENDING_ONLY_CANCEL',
    ('CONFLICT', '仅待支付订单可标记为已支付，当前状态：'): 'ORDER_PENDING_ONLY_MARK_PAID',
    ('CONFLICT', '仅已支付/已发放订单可退款，当前状态：'): 'ORDER_REFUND_STATUS_INVALID',
    ('BAD_REQUEST', '退款金额不能超过订单金额'): 'ORDER_REFUND_AMOUNT_EXCEEDED',
    ('CONFLICT', '仅待支付订单可发起支付'): 'ORDER_PENDING_ONLY_PREPAY',
    ('CONFLICT', '权限码已存在：'): 'PERMISSION_CODE_EXISTS',
    ('CONFLICT', '角色编码已存在：'): 'ROLE_CODE_EXISTS',
    ('BAD_REQUEST', '权限点不存在：'): 'PERMISSION_NOT_FOUND',
    ('NOT_FOUND', '权限点不存在：'): 'PERMISSION_NOT_FOUND',
    ('NOT_FOUND', '会员记录不存在：'): 'MEMBERSHIP_NOT_FOUND',
    ('NOT_FOUND', 'Banner 不存在：'): 'BANNER_NOT_FOUND',
    ('NOT_FOUND', '标签不存在：'): 'CONTENT_TAG_NOT_FOUND',
    ('CONFLICT', '仅待审核内容可审核，当前状态：'): 'CONTENT_AUDIT_STATUS_INVALID',
    ('CONFLICT', '仅已发布内容可下线，当前状态：'): 'CONTENT_OFFLINE_STATUS_INVALID',
    ('BAD_REQUEST', '非法状态：'): 'TENANT_STATUS_INVALID',
    ('CONFLICT', '租户编码已存在：'): 'TENANT_CODE_EXISTS',
    ('BAD_REQUEST', '权限点不存在：'): 'PERMISSION_NOT_FOUND',
}

PATTERN = re.compile(
    r'throw new ServiceException\("([A-Z_]+)",\s*(.+)\);'
)


def transform_throw(match: re.Match[str]) -> str:
    code, msg_expr = match.group(1), match.group(2)
    if msg_expr.startswith('"') and msg_expr.endswith('"'):
        msg = msg_expr[1:-1]
        key = (code, msg)
        if key in EXACT:
            return f'Errors.throwError(PlatformErrorCode.{EXACT[key]})'
        for (c, prefix), enum_name in PREFIX.items():
            if c == code and prefix and msg.startswith(prefix):
                if msg == prefix.rstrip('：'):
                    return f'Errors.throwError(PlatformErrorCode.{enum_name})'
                rest = msg_expr if '+' in msg_expr else f'{msg_expr}'
                # dynamic suffix
                if '+' in msg_expr:
                    return f'Errors.throwError(PlatformErrorCode.{enum_name}, {msg_expr})'
                detail = msg[len(prefix):] if len(msg) > len(prefix) else msg
                if detail:
                    return f'Errors.throwError(PlatformErrorCode.{enum_name}, {msg_expr})'
                return f'Errors.throwError(PlatformErrorCode.{enum_name})'
        for (c, prefix), enum_name in PREFIX.items():
            if c == code and prefix and msg.startswith(prefix):
                return f'Errors.throwError(PlatformErrorCode.{enum_name}, {msg_expr})'
    else:
        # dynamic message expression
        for (c, prefix), enum_name in PREFIX.items():
            if c == code and prefix and prefix in msg_expr:
                return f'Errors.throwError(PlatformErrorCode.{enum_name}, {msg_expr})'
        if code == 'FORBIDDEN' and '未开放' in msg_expr:
            return f'Errors.throwError(PlatformErrorCode.REGISTER_CHANNEL_CLOSED, {msg_expr})'
        if code == 'FORBIDDEN' and 'getReason' in msg_expr:
            return f'Errors.throwError(PlatformErrorCode.FORBIDDEN, {msg_expr})'
        if code == 'FORBIDDEN' and '缺少权限' in msg_expr or code == 'FORBIDDEN' and 'permission' in msg_expr.lower():
            return f'Errors.throwError(PlatformErrorCode.PERMISSION_DENIED, {msg_expr})'
        if code == 'BAD_REQUEST' and '学习模式' in msg_expr:
            return f'Errors.throwError(PlatformErrorCode.BAD_REQUEST, {msg_expr})'
        if code == 'NOT_FOUND' and '角色不存在' in msg_expr:
            return f'Errors.throwError(PlatformErrorCode.ROLE_NOT_FOUND, {msg_expr})'
        if code == 'BAD_REQUEST' and '权限点不存在' in msg_expr:
            return f'Errors.throwError(PlatformErrorCode.PERMISSION_NOT_FOUND, {msg_expr})'
        if code == 'BAD_REQUEST' and '未知额度桶' in msg_expr:
            return f'Errors.throwError(PlatformErrorCode.QUOTA_BUCKET_UNKNOWN, {msg_expr})'
    return match.group(0)


def ensure_imports(content: str) -> str:
    if 'Errors.throwError' not in content and 'Errors.of' not in content:
        return content
    if 'import cn.cyc.ai.cog.common.exception.Errors;' in content:
        return content
    pkg_end = content.find(';', content.find('package '))
    first_import = content.find('import ')
    insert = 'import cn.cyc.ai.cog.common.exception.Errors;\nimport cn.cyc.ai.cog.common.exception.PlatformErrorCode;\n'
    if first_import >= 0:
        return content[:first_import] + insert + content[first_import:]
    return content[:pkg_end + 1] + '\n\n' + insert + content[pkg_end + 1:]


def process_file(path: Path) -> bool:
    if 'ServiceException.java' == path.name or 'Errors.java' == path.name or 'PlatformErrorCode.java' == path.name:
        return False
    text = path.read_text(encoding='utf-8')
    if 'new ServiceException(' not in text:
        return False
    new_text = PATTERN.sub(transform_throw, text)
    # orElseThrow lambdas
    new_text = new_text.replace(
        '.orElseThrow(() -> new ServiceException("BAD_REQUEST", "不支持的支付通道：" + channel))',
        '.orElseThrow(() -> Errors.of(PlatformErrorCode.PAYMENT_CHANNEL_UNSUPPORTED, "不支持的支付通道：" + channel))',
    )
    new_text = new_text.replace(
        '.orElseThrow(() -> new ServiceException("BAD_REQUEST", "不支持的学习模式：" + request.getMode()))',
        '.orElseThrow(() -> Errors.of(PlatformErrorCode.BAD_REQUEST, "不支持的学习模式：" + request.getMode()))',
    )
    new_text = ensure_imports(new_text)
    if new_text != text:
        path.write_text(new_text, encoding='utf-8')
        return True
    return False


def main() -> None:
    changed = []
    for path in ROOT.rglob('*.java'):
        if 'target' in path.parts or '.mvn' in path.parts:
            continue
        if process_file(path):
            changed.append(path)
    print(f'Updated {len(changed)} files')
    for p in changed:
        print(' -', p.relative_to(ROOT))


if __name__ == '__main__':
    main()
