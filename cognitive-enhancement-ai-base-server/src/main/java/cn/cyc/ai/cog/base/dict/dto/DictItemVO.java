package cn.cyc.ai.cog.base.dict.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典项列表视图（支持树形 children）。
 */
@Data
public class DictItemVO {

    private Long id;

    private String bizCode;

    private Long typeId;

    private Long parentId;

    private String value;

    private String label;

    private String enLabel;

    private String remark;

    private Integer sort;

    private Boolean status;

    private List<DictItemVO> children = new ArrayList<>();
}
