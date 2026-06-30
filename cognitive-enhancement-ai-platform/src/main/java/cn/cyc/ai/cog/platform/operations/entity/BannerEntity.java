package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 运营位 / Banner 实体（映射 qz_ops_banner）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_banner")
public class BannerEntity extends BaseEntity {

    /** 标题。 */
    private String title;

    /** 图片 URL。 */
    private String imageUrl;

    /** 跳转链接。 */
    private String linkUrl;

    /** 投放位置：HOME_TOP/SIDEBAR 等。 */
    private String position;

    /** 排序号。 */
    private Integer sortNo;

    /** 状态：ENABLED/DISABLED。 */
    private String status;

    /** 投放开始时间。 */
    private LocalDateTime startTime;

    /** 投放结束时间。 */
    private LocalDateTime endTime;
}
