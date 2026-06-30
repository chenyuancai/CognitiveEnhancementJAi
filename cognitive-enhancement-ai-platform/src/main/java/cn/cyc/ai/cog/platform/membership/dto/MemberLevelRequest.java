package cn.cyc.ai.cog.platform.membership.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员等级调整请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MemberLevelRequest {

    /** 主键 ID */
    private Long id;

    /** 等级编码。 */
    @NotBlank(message = "会员等级编码不能为空")
    private String levelCode;

    /** 等级ID */
    private Long levelId;

    /** expireAt。 */
    private LocalDateTime expireAt;

    /** remark。 */
    private String remark;
}
