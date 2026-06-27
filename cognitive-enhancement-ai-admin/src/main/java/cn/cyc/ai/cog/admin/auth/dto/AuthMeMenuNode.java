package cn.cyc.ai.cog.admin.auth.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AuthMeMenuNode {
    private String key;
    private String title;
    private String path;
    private String icon;
    private List<AuthMeMenuNode> children = new ArrayList<>();
}
