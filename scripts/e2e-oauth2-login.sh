#!/usr/bin/env bash
# OAuth2 三服务联调：Auth 发 Token → Gateway 验签 → Starter /me
set -euo pipefail

AUTH_BASE="${AUTH_BASE:-http://127.0.0.1:8802}"
GATEWAY_BASE="${GATEWAY_BASE:-http://127.0.0.1:8801}"
CLIENT_ID="${CLIENT_ID:-cms-client}"
CLIENT_SECRET="${CLIENT_SECRET:-cms-secret}"
USERNAME="${USERNAME:-admin}"
PASSWORD="${PASSWORD:-user1234}"

echo "==> 1. Auth password grant 获取 access_token"
TOKEN_RESPONSE="$(curl -sf -X POST "${AUTH_BASE}/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u "${CLIENT_ID}:${CLIENT_SECRET}" \
  -d "grant_type=password&username=${USERNAME}&password=${PASSWORD}&scope=cms.read")"

ACCESS_TOKEN="$(python3 -c "import json,sys; print(json.load(sys.stdin)['access_token'])" <<<"${TOKEN_RESPONSE}")"
if [[ -z "${ACCESS_TOKEN}" ]]; then
  echo "获取 access_token 失败: ${TOKEN_RESPONSE}" >&2
  exit 1
fi
echo "    access_token 已获取 (${#ACCESS_TOKEN} chars)"

echo "==> 2. 经 Gateway 访问 /api/admin/auth/me"
ME_RESPONSE="$(curl -sf "${GATEWAY_BASE}/api/admin/auth/me" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}")"

echo "${ME_RESPONSE}" | python3 -c "
import json, sys
body = json.load(sys.stdin)
assert body.get('success') is True, body
user = body['data']['user']['username']
print(f'    /me 成功，用户: {user}')
"

echo "==> 3. 无 Token 应返回 401"
HTTP_CODE="$(curl -s -o /dev/null -w '%{http_code}' "${GATEWAY_BASE}/api/admin/auth/me")"
if [[ "${HTTP_CODE}" != "401" ]]; then
  echo "期望 401，实际 ${HTTP_CODE}" >&2
  exit 1
fi
echo "    无 Token 返回 ${HTTP_CODE}（符合预期）"

echo ""
echo "OAuth2 全链路联调通过 ✅"
