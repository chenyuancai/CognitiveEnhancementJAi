package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

import java.util.List;

/**
 * 权限树分组节点（对齐前端 PermissionGroupDefinition）。
 *
 * @author cyc
 */
@Data
public class PermissionTreeGroup {

    private String scope;
    private String key;
    private String name;
    private List<PermissionTreeItem> items;

    @Data
    public static class PermissionTreeItem {
        private String code;
        private String name;
        private String kind;
        private String bindKey;
        private String path;
    }
}
