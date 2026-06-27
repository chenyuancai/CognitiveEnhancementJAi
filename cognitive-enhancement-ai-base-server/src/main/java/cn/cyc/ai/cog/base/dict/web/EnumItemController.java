package cn.cyc.ai.cog.base.dict.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.dict.dto.DictItemListQuery;
import cn.cyc.ai.cog.base.dict.dto.DictItemSaveRequest;
import cn.cyc.ai.cog.base.dict.dto.DictItemVO;
import cn.cyc.ai.cog.base.dict.service.DictItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 枚举项管理。
 */
@Tag(name = "基础-枚举项", description = "枚举项 CRUD 与列表/树查询")
@RestController
@RequestMapping("/api/base/enum/items")
public class EnumItemController {

    private final DictItemService dictItemService;

    public EnumItemController(DictItemService dictItemService) {
        this.dictItemService = dictItemService;
    }

    @Operation(summary = "保存枚举项", description = "value 必须为整数")
    @PostMapping("/save")
    public ApiResponse<DictItemVO> save(@Valid @RequestBody DictItemSaveRequest request) {
        return ApiResponse.success(dictItemService.save(request, true));
    }

    @Operation(summary = "删除枚举项")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(dictItemService.delete(id));
    }

    @Operation(summary = "枚举项列表")
    @PostMapping("/list")
    public ApiResponse<List<DictItemVO>> list(@Valid @RequestBody DictItemListQuery query) {
        return ApiResponse.success(dictItemService.list(query));
    }
}
