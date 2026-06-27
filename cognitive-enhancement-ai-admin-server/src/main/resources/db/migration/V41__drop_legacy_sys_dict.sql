-- 下线旧版 platform 字典表，banner 位种子直接写入 qz_base_dict_*

INSERT INTO qz_base_dict_type (tenant_id, biz_code, dict_kind, share_scope, code, name, en_name, status)
SELECT 1, 'cog', 1, 'global', 'banner_position', 'Banner 展示位', 'banner_position', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM qz_base_dict_type
    WHERE biz_code = 'cog' AND dict_kind = 1 AND share_scope = 'global'
      AND tenant_id = 1 AND code = 'banner_position' AND deleted = 0
);

INSERT INTO qz_base_dict_item (tenant_id, biz_code, type_id, parent_id, `value`, label, en_label, sort, status)
SELECT 1, 'cog', bt.id, 0, 'HOME_TOP', '首页顶部', 'HOME_TOP', 1, 1
FROM qz_base_dict_type bt
WHERE bt.code = 'banner_position' AND bt.biz_code = 'cog' AND bt.deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM qz_base_dict_item bi
    WHERE bi.type_id = bt.id AND bi.`value` = 'HOME_TOP' AND bi.deleted = 0
);

INSERT INTO qz_base_dict_item (tenant_id, biz_code, type_id, parent_id, `value`, label, en_label, sort, status)
SELECT 1, 'cog', bt.id, 0, 'SIDEBAR', '侧边栏', 'SIDEBAR', 2, 1
FROM qz_base_dict_type bt
WHERE bt.code = 'banner_position' AND bt.biz_code = 'cog' AND bt.deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM qz_base_dict_item bi
    WHERE bi.type_id = bt.id AND bi.`value` = 'SIDEBAR' AND bi.deleted = 0
);

INSERT INTO qz_base_dict_item (tenant_id, biz_code, type_id, parent_id, `value`, label, en_label, sort, status)
SELECT 1, 'cog', bt.id, 0, 'POPUP', '弹窗', 'POPUP', 3, 1
FROM qz_base_dict_type bt
WHERE bt.code = 'banner_position' AND bt.biz_code = 'cog' AND bt.deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM qz_base_dict_item bi
    WHERE bi.type_id = bt.id AND bi.`value` = 'POPUP' AND bi.deleted = 0
);

DROP TABLE IF EXISTS qz_sys_dict_item;
DROP TABLE IF EXISTS qz_sys_dict_type;
