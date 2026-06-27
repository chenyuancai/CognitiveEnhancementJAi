package cn.cyc.ai.cog.platform.operations.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("qz_ops_in_app_message")
public class InAppMessageEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long userId;
    private String templateCode;
    private String title;
    private String content;
    private Integer readFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
