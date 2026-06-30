-- 方案 B：清空无法用当前 master-key 解密的旧 api_key，请在 CMS 重新配置 bailian 提供商 apiKey

UPDATE qz_ai_model_provider_binding
SET api_key = NULL
WHERE api_key IS NOT NULL;

UPDATE qz_ai_model_provider
SET api_key = NULL
WHERE provider_code = 'bailian'
  AND api_key IS NOT NULL;
