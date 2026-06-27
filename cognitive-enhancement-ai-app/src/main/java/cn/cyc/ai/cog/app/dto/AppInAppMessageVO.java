package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppInAppMessageVO {

    private Long id;
    private String templateCode;
    private String title;
    private String content;
    private boolean read;
    private LocalDateTime createTime;
}
