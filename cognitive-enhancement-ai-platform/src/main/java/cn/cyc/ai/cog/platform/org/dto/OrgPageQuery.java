package cn.cyc.ai.cog.platform.org.dto;

import lombok.Data;

@Data
public class OrgPageQuery {

    private long current = 1;
    private long size = 20;
    private String keyword;
    private String segment;
}
