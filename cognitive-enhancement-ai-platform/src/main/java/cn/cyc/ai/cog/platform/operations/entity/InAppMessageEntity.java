package cn.cyc.ai.cog.platform.operations.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * InC端消息实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_ops_in_app_message")
public class InAppMessageEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 租户 ID */
    private Long tenantId;
    /** 用户 ID */
    private Long userId;
    /** template编码。 */
    private String templateCode;
    /** 标题。 */
    private String title;
    /** 内容。 */
    private String content;
    /** read标记。 */
    private Integer readFlag;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 逻辑删除标记 */
    private Integer deleted;
}
