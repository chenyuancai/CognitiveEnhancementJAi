ALTER TABLE qz_rt_conversation_session
    ADD KEY idx_conversation_session_user_updated (tenant_id, user_id, capability_code, updated_at);
