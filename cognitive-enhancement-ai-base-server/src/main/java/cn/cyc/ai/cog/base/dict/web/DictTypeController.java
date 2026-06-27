package cn.cyc.ai.cog.base.dict.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.base.dict.dto.DictTypePageQuery;
import cn.cyc.ai.cog.base.dict.dto.DictTypeSaveRequest;
import cn.cyc.ai.cog.base.dict.dto.DictTypeTreeVO;
import cn.cyc.ai.cog.base.dict.dto.DictTypeVO;
import cn.cyc.ai.cog.base.dict.enums.DictKindEnum;
import cn.cyc.ai.cog.base.dict.service.DictTypeService;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典类型管理（字符串值）。
 */
@Tag(name = "基础-字典类型", description = "字典类型 CRUD 与树查询")
@RestController
@RequestMapping("/api/base/dict/types")
public class DictTypeController {

    private final DictTypeService dictTypeService;

    public DictTypeController(DictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    @Operation(summary = "保存字典类型")
    @PostMapping("/save")
    public ApiResponse<DictTypeVO> save(@Valid @RequestBody DictTypeSaveRequest request) {
        return ApiResponse.success(dictTypeService.save(DictKindEnum.DICT.getValue(), request));
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(dictTypeService.delete(id));
    }

    @Operation(summary = "字典类型分页")
    @PostMapping("/page")
    public ApiResponse<PageResult<DictTypeVO>> page(@RequestBody DictTypePageQuery query) {
        return ApiResponse.success(dictTypeService.page(DictKindEnum.DICT.getValue(), query));
    }

    @Operation(summary = "字典类型树", description = "返回类型及其项的树形结构")
    @GetMapping("/tree")
    public ApiResponse<List<DictTypeTreeVO>> tree(@RequestParam(required = false) String code) {
        return ApiResponse.success(dictTypeService.tree(DictKindEnum.DICT.getValue(), code));
    }

    @Operation(summary = "按编码查询字典类型")
    @GetMapping("/get/{code}")
    public ApiResponse<DictTypeVO> getByCode(@PathVariable String code) {
        return ApiResponse.success(dictTypeService.getByCode(DictKindEnum.DICT.getValue(), code));
    }
}
