package cn.cyc.ai.cog.admin.operation.web;

import cn.cyc.ai.cog.admin.operation.dto.MessageTemplateVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.MessageTemplate;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplatePageQuery;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplatePreviewRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateRenderResult;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateSaveRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateSendRequest;
import cn.cyc.ai.cog.platform.operations.service.MessageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "运营-消息模板", description = "消息/通知模板管理")
@RestController
@RequestMapping("/api/admin/operations/message-templates")
public class MessageTemplateAdminController {

    private final MessageTemplateService messageTemplateService;

    public MessageTemplateAdminController(MessageTemplateService messageTemplateService) {
        this.messageTemplateService = messageTemplateService;
    }

    @Operation(summary = "消息模板分页")
    @RequirePermission("admin:banner:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<MessageTemplateVO>> page(@RequestBody MessageTemplatePageQuery query) {
        return ApiResponse.success(messageTemplateService.page(query).map(this::toVo));
    }

    @Operation(summary = "消息模板详情")
    @RequirePermission("admin:banner:update")
    @GetMapping("/{id}")
    public ApiResponse<MessageTemplateVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(messageTemplateService.detail(id)));
    }

    @Operation(summary = "新增消息模板")
    @RequirePermission("admin:banner:create")
    @PostMapping
    public ApiResponse<MessageTemplateVO> create(@Valid @RequestBody MessageTemplateSaveRequest request) {
        return ApiResponse.success(toVo(messageTemplateService.create(request)));
    }

    @Operation(summary = "编辑消息模板")
    @RequirePermission("admin:banner:update")
    @PostMapping("/update")
    public ApiResponse<MessageTemplateVO> update(@Valid @RequestBody MessageTemplateSaveRequest request) {
        return ApiResponse.success(toVo(messageTemplateService.update(request.getId(), request)));
    }

    @Operation(summary = "删除消息模板")
    @RequirePermission("admin:banner:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        messageTemplateService.delete(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "渲染预览", description = "按 variable_schema 校验变量并替换占位符。")
    @RequirePermission("admin:banner:update")
    @PostMapping("/preview")
    public ApiResponse<MessageTemplateRenderResult> preview(@RequestBody(required = false) MessageTemplatePreviewRequest request) {
        MessageTemplatePreviewRequest body = request == null ? new MessageTemplatePreviewRequest() : request;
        return ApiResponse.success(messageTemplateService.preview(body.getId(), body));
    }

    @Operation(summary = "发送消息", description = "按模板渲染后走对应通道（IN_APP/EMAIL/SMS）。")
    @RequirePermission("admin:banner:update")
    @PostMapping("/send")
    public ApiResponse<MessageSendResult> send(@Valid @RequestBody MessageTemplateSendRequest request) {
        return ApiResponse.success(messageTemplateService.sendByTemplateId(
                request.getId(), request.getRecipient(), request.getParams()));
    }

    private MessageTemplateVO toVo(MessageTemplate template) {
        MessageTemplateVO vo = new MessageTemplateVO();
        vo.setId(template.id());
        vo.setTemplateCode(template.templateCode());
        vo.setTemplateName(template.templateName());
        vo.setChannel(template.channel());
        vo.setContent(template.content());
        vo.setVariableSchema(template.variableSchema());
        vo.setStatus(template.status());
        return vo;
    }
}
