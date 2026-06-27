-- 模型提供商 API Key：统一在提供商表维护，绑定表可选覆盖

ALTER TABLE qz_ai_model_provider
    ADD COLUMN api_key VARCHAR(512) NULL COMMENT 'API Key（明文，仅服务端使用）' AFTER default_credential_ref;

ALTER TABLE qz_ai_model_provider_binding
    ADD COLUMN api_key VARCHAR(512) NULL COMMENT '绑定级 API Key 覆盖' AFTER credential_ref;
