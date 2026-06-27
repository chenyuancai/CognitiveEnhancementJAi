package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型树视图（类型 + 项树）。
 */
@Data
public class DictTypeTreeVO {

    private Long id;

    private String bizCode;

    private String shareScope;

    private String code;

    private String name;

    private String enName;

    private String description;

    private String remark;

    private Boolean status;

    private List<DictItemVO> detailList = new ArrayList<>();
}
