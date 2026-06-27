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

@Service
public class MessageTemplateService {

    private final MessageTemplateRepository messageTemplateRepository;
    private final MessageTemplateRenderer messageTemplateRenderer;
    private final List<MessageSender> messageSenders;

    public MessageTemplateService(MessageTemplateRepository messageTemplateRepository,
                                  MessageTemplateRenderer messageTemplateRenderer,
                                  List<MessageSender> messageSenders) {
        this.messageTemplateRepository = messageTemplateRepository;
        this.messageTemplateRenderer = messageTemplateRenderer;
        this.messageSenders = messageSenders;
    }

    public PageResult<MessageTemplate> page(MessageTemplatePageQuery query) {
        return messageTemplateRepository.page(query);
    }

    public MessageTemplate detail(Long id) {
        return messageTemplateRepository.findById(id);
    }

    public MessageTemplate create(MessageTemplateSaveRequest request) {
        return messageTemplateRepository.create(request);
    }

    public MessageTemplate update(Long id, MessageTemplateSaveRequest request) {
        return messageTemplateRepository.update(id, request);
    }

    public void delete(Long id) {
        messageTemplateRepository.delete(id);
    }

    public MessageTemplateRenderResult preview(Long id, MessageTemplatePreviewRequest request) {
        MessageTemplate template = messageTemplateRepository.findById(id);
        Map<String, ?> params = request == null || request.getParams() == null ? Map.of() : request.getParams();
        String rendered = messageTemplateRenderer.render(template, params);
        return new MessageTemplateRenderResult(template.templateCode(), template.channel(), rendered);
    }

    public MessageTemplateRenderResult renderByCode(String templateCode, Map<String, ?> params) {
        MessageTemplate template = messageTemplateRepository.findByCode(templateCode);
        String rendered = messageTemplateRenderer.render(template, params);
        return new MessageTemplateRenderResult(template.templateCode(), template.channel(), rendered);
    }

    public MessageSendResult send(String templateCode, String recipient, Map<String, ?> params) {
        MessageTemplateRenderResult rendered = renderByCode(templateCode, params);
        MessageSendRequest sendRequest = new MessageSendRequest(
                rendered.channel(),
                recipient,
                rendered.templateCode(),
                rendered.renderedContent());
        return dispatch(sendRequest);
    }

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
