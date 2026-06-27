package cn.cyc.ai.cog.platform.iam.dto;

import lombok.Data;

@Data
public class UserStatusUpdateRequest {

    private Long id;

    private String status;
    private String banReason;
    private java.time.LocalDateTime banUntil;
}
