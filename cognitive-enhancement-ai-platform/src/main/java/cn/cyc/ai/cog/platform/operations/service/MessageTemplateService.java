package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.MessageTemplate;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageSendResult;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplatePageQuery;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplatePreviewRequest;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateRenderResult;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateSaveRequest;
import cn.cyc.ai.cog.platform.operations.repository.MessageTemplateRepository;
import cn.cyc.ai.cog.platform.operations.spi.MessageSender;
import cn.cyc.ai.cog.platform.operations.support.MessageTemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 消息Template服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class MessageTemplateService {

    /** 消息Template仓储。 */
    private final MessageTemplateRepository messageTemplateRepository;
    /** 消息TemplateRenderer。 */
    private final MessageTemplateRenderer messageTemplateRenderer;
    /** 消息Senders。 */
    private final List<MessageSender> messageSenders;

    /**
     * 创建消息Template服务。
     */
    public MessageTemplateService(MessageTemplateRepository messageTemplateRepository,
                                  MessageTemplateRenderer messageTemplateRenderer,
                                  List<MessageSender> messageSenders) {
        this.messageTemplateRepository = messageTemplateRepository;
        this.messageTemplateRenderer = messageTemplateRenderer;
        this.messageSenders = messageSenders;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<MessageTemplate> page(MessageTemplatePageQuery query) {
        return messageTemplateRepository.page(query);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public MessageTemplate detail(Long id) {
        return messageTemplateRepository.findById(id);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public MessageTemplate create(MessageTemplateSaveRequest request) {
        return messageTemplateRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public MessageTemplate update(Long id, MessageTemplateSaveRequest request) {
        return messageTemplateRepository.update(id, request);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public void delete(Long id) {
        messageTemplateRepository.delete(id);
    }

    /**
     * 执行preview。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
    public MessageTemplateRenderResult preview(Long id, MessageTemplatePreviewRequest request) {
        MessageTemplate template = messageTemplateRepository.findById(id);
        Map<String, ?> params = request == null || request.getParams() == null ? Map.of() : request.getParams();
        String rendered = messageTemplateRenderer.render(template, params);
        return new MessageTemplateRenderResult(template.templateCode(), template.channel(), rendered);
    }

    /**
     * 执行render人编码。
     *
     * @param templateCode template编码
     * @param params params
     * @return 执行结果
     */
    public MessageTemplateRenderResult renderByCode(String templateCode, Map<String, ?> params) {
        MessageTemplate template = messageTemplateRepository.findByCode(templateCode);
        String rendered = messageTemplateRenderer.render(template, params);
        return new MessageTemplateRenderResult(template.templateCode(), template.channel(), rendered);
    }

    /**
     * 执行send。
     *
     * @param templateCode template编码
     * @param recipient recipient
     * @param params params
     * @return 执行结果
     */
    public MessageSendResult send(String templateCode, String recipient, Map<String, ?> params) {
        MessageTemplateRenderResult rendered = renderByCode(templateCode, params);
        MessageSendRequest sendRequest = new MessageSendRequest(
                rendered.channel(),
                recipient,
                rendered.templateCode(),
                rendered.renderedContent());
        return dispatch(sendRequest);
    }

    /**
     * 执行send人TemplateID。
     *
     * @param id 主键 ID
     * @param recipient recipient
     * @param params params
     * @return 执行结果
     */
    public MessageSendResult sendByTemplateId(Long id, String recipient, Map<String, ?> params) {
        MessageTemplate template = messageTemplateRepository.findById(id);
        Map<String, ?> safeParams = params == null ? Map.of() : params;
        String rendered = messageTemplateRenderer.render(template, safeParams);
        MessageSendRequest sendRequest = new MessageSendRequest(
                template.channel(),
                recipient,
                template.templateCode(),
                rendered);
        return dispatch(sendRequest);
    }

    /**
     * 执行dispatch。
     *
     * @param request 请求
     * @return 执行结果
     */
    private MessageSendResult dispatch(MessageSendRequest request) {
        String channel = request.channel() == null ? "" : request.channel().trim().toUpperCase();
        for (MessageSender sender : messageSenders) {
            if (sender.supports(channel)) {
                return sender.send(request);
            }
        }
        MessageSender fallback = messageSenders.isEmpty() ? null : messageSenders.get(messageSenders.size() - 1);
        if (fallback != null) {
            return fallback.send(request);
        }
        return new MessageSendResult(false, channel, null, "no sender");
    }
}
