-- 知识内容域默认演示数据：1 篇已发布内容 + 1 标签 + 1 知识包 + 1 条目（挂载该内容）
INSERT IGNORE INTO qz_kb_content (
    id, tenant_id, title, content_type, author, status, summary, body,
    min_level_code, current_version, published_at
) VALUES (
    1, 1, '启知入门指南', 'ARTICLE', '系统', 'PUBLISHED',
    '面向新用户的平台入门说明', '欢迎使用启知认知增强平台。本文介绍知识浏览、学习与会员额度等基础能力。',
    'FREE', 1, CURRENT_TIMESTAMP
);

INSERT IGNORE INTO qz_kb_content_tag (id, tenant_id, tag_name, tag_color) VALUES
    (1, 1, '入门', '#409EFF');

INSERT IGNORE INTO qz_kb_content_tag_rel (content_id, tag_id) VALUES (1, 1);

INSERT IGNORE INTO qz_kb_knowledge_package (id, tenant_id, package_name, description, status) VALUES
    (1, 1, '默认知识包', '系统内置演示知识包，挂载入门内容', 'ENABLED');

INSERT IGNORE INTO qz_kb_knowledge_package_item (id, package_id, parent_id, content_id, title, sort_no) VALUES
    (1, 1, 0, 1, '启知入门指南', 1);
