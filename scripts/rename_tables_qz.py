#!/usr/bin/env python3
"""批量将旧表名替换为 qz_ 前缀新表名（按长度降序，避免子串误替换）。"""
import os
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

REPLACEMENTS = [
    ("center_capability_tenant_binding", "qz_ai_capability_tenant_binding"),
    ("center_capability_release_pointer", "qz_ai_capability_release_pointer"),
    ("center_prompt_release_pointer", "qz_ai_prompt_release_pointer"),
    ("runtime_scenario_knowledge_binding", "qz_rt_scenario_knowledge_binding"),
    ("runtime_conversation_message", "qz_rt_conversation_message"),
    ("runtime_conversation_session", "qz_rt_conversation_session"),
    ("runtime_execution_feedback", "qz_rt_execution_feedback"),
    ("runtime_knowledge_fragment", "qz_rt_knowledge_fragment"),
    ("runtime_file_upload_record", "qz_rt_file_upload_record"),
    ("center_capability_definition", "qz_ai_capability_definition"),
    ("biz_knowledge_package_item", "qz_kb_knowledge_package_item"),
    ("biz_membership_change_log", "qz_mbr_membership_change_log"),
    ("biz_account_membership", "qz_mbr_account"),
    ("biz_subscription_package", "qz_bill_subscription_package"),
    ("center_model_definition", "qz_ai_model_definition"),
    ("center_prompt_template", "qz_ai_prompt_template"),
    ("center_tool_definition", "qz_ai_tool_definition"),
    ("center_skill_definition", "qz_ai_skill_definition"),
    ("center_agent_definition", "qz_ai_agent_definition"),
    ("runtime_execution_record", "qz_rt_execution_record"),
    ("runtime_file_parse_task", "qz_rt_file_parse_task"),
    ("biz_content_import_job", "qz_kb_content_import_job"),
    ("biz_knowledge_package", "qz_kb_knowledge_package"),
    ("biz_quota_member_alloc", "qz_mbr_quota_member_alloc"),
    ("biz_membership_level", "qz_mbr_level"),
    ("biz_financial_record", "qz_bill_financial_record"),
    ("biz_message_template", "qz_ops_message_template"),
    ("runtime_usage_account", "qz_rt_usage_account"),
    ("runtime_usage_record", "qz_rt_usage_record"),
    ("runtime_trace_span", "qz_rt_trace_span"),
    ("runtime_audit_log", "qz_rt_audit_log"),
    ("harness_step_report", "qz_rt_harness_step_report"),
    ("sys_role_permission", "qz_iam_role_permission"),
    ("sys_security_config", "qz_sys_security_config"),
    ("sys_feature_switch", "qz_sys_feature_switch"),
    ("biz_org_department", "qz_acct_org_department"),
    ("biz_subscription", "qz_bill_subscription"),
    ("biz_quota_account", "qz_mbr_quota_account"),
    ("biz_quota_package", "qz_bill_quota_package"),
    ("biz_content_tag_rel", "qz_kb_content_tag_rel"),
    ("biz_token_record", "qz_mbr_token_record"),
    ("biz_announcement", "qz_ops_announcement"),
    ("biz_organization", "qz_acct_org"),
    ("center_skill_tool", "qz_ai_skill_tool"),
    ("center_agent_skill", "qz_ai_agent_skill"),
    ("biz_org_member", "qz_acct_org_member"),
    ("biz_content_tag", "qz_kb_content_tag"),
    ("harness_report", "qz_rt_harness_report"),
    ("sys_permission", "qz_iam_permission"),
    ("sys_dict_type", "qz_sys_dict_type"),
    ("sys_dict_item", "qz_sys_dict_item"),
    ("sys_user_role", "qz_iam_user_role"),
    ("sys_audit_log", "qz_sys_audit_log"),
    ("biz_account", "qz_acct_account"),
    ("biz_content", "qz_kb_content"),
    ("biz_banner", "qz_ops_banner"),
    ("biz_order", "qz_bill_order"),
    ("sys_tenant", "qz_iam_tenant"),
    ("sys_user", "qz_iam_user"),
    ("sys_role", "qz_iam_role"),
]

SKIP_DIRS = {".git", "target", ".mvn", "node_modules", "scripts"}
SKIP_FILES = {"V25__unify_table_prefix.sql", "rename_tables_qz.py"}


def should_process(path: Path) -> bool:
    if any(part in SKIP_DIRS for part in path.parts):
        return False
    if path.name in SKIP_FILES:
        return False
    if path.suffix not in {".java", ".sql", ".yml", ".md", ".xml"}:
        return False
    # 历史 Flyway 迁移 V1-V24 保持原表名（已由 V25 RENAME）
    if path.suffix == ".sql" and "/db/migration/V" in str(path):
        name = path.name
        if name.startswith("V") and not name.startswith("V25"):
            return False
    return True


def replace_content(text: str) -> tuple[str, int]:
    count = 0
    for old, new in REPLACEMENTS:
        n = text.count(old)
        if n:
            text = text.replace(old, new)
            count += n
    return text, count


def main():
    total_files = 0
    total_replacements = 0
    for root, dirs, files in os.walk(ROOT):
        dirs[:] = [d for d in dirs if d not in SKIP_DIRS]
        for name in files:
            path = Path(root) / name
            if not should_process(path):
                continue
            try:
                original = path.read_text(encoding="utf-8")
            except (UnicodeDecodeError, OSError):
                continue
            updated, n = replace_content(original)
            if n > 0:
                path.write_text(updated, encoding="utf-8")
                total_files += 1
                total_replacements += n
                print(f"  {path.relative_to(ROOT)} ({n})")
    print(f"\nDone: {total_files} files, {total_replacements} replacements")


if __name__ == "__main__":
    main()
