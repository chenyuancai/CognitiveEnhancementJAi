package cn.cyc.ai.cog.platform.membership.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员等级调整请求。
 */
@Data
public class MemberLevelRequest {

    private Long id;

    @NotBlank(message = "会员等级编码不能为空")
    private String levelCode;

    private Long levelId;

    private LocalDateTime expireAt;

    private String remark;
}
