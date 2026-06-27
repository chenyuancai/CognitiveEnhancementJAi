-- 注册赠送 Free 会员包对应的订阅套餐（非在售，仅供开户写入 qz_bill_subscription）
INSERT INTO qz_bill_subscription_package (
    tenant_id, package_code, package_name, segment, level_id,
    billing_period, period_count, trial_days, price_fen, cycle_token_quota,
    seat_count, sale_mode, require_contract, status
)
SELECT
    l.tenant_id,
    'sub.free.default',
    '免费版（注册赠送）',
    l.segment,
    l.id,
    'MONTH',
    1,
    0,
    0,
    100000,
    1,
    'GIFT',
    0,
    'OFF_SALE'
FROM qz_mbr_level l
WHERE l.level_code = 'FREE'
  AND NOT EXISTS (
      SELECT 1 FROM qz_bill_subscription_package p WHERE p.package_code = 'sub.free.default'
  );
