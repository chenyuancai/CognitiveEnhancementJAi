package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AuthMeMenuNode
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AuthMeMenuNode {
    /** 键。 */
    private String key;
    /** 标题。 */
    private String title;
    /** 路径。 */
    private String path;
    /** icon。 */
    private String icon;
    /** children。 */
    private List<AuthMeMenuNode> children = new ArrayList<>();
}
