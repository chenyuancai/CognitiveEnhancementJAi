package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

import java.util.List;

/**
 * 权限树分组节点（对齐前端 PermissionGroupDefinition）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class PermissionTreeGroup {

    /** scope。 */
    private String scope;
    /** 键。 */
    private String key;
    /** 名称。 */
    private String name;
    /** items。 */
    private List<PermissionTreeItem> items;

    /**
     * PermissionTreeItem
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    @Data
    public static class PermissionTreeItem {
        /** 编码。 */
        private String code;
        /** 名称。 */
        private String name;
        /** kind。 */
        private String kind;
        /** bind键。 */
        private String bindKey;
        /** 路径。 */
        private String path;
    }
}
