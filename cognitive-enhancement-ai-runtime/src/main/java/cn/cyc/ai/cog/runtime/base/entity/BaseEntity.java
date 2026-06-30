package cn.cyc.ai.cog.runtime.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 基础实体类。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class BaseEntity {
    /** 主键 ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
