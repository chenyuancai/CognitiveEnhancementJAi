package cn.cyc.ai.cog.base.dict.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.dict.dto.DictItemReadVO;
import cn.cyc.ai.cog.base.dict.service.DictItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公共字典只读接口。
 */
@Tag(name = "基础-字典读取", description = "启用字典项下拉读取")
@RestController
@RequestMapping("/api/base/dict")
public class DictReadController {

    private final DictItemService dictItemService;

    public DictReadController(DictItemService dictItemService) {
        this.dictItemService = dictItemService;
    }

    @Operation(summary = "按类型编码读取启用字典项", description = "供前端下拉/Tag 渲染")
    @GetMapping("/{code}/items")
    public ApiResponse<List<DictItemReadVO>> items(@PathVariable String code) {
        return ApiResponse.success(dictItemService.listEnabledByCode(code));
    }
}
