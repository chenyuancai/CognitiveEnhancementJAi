package cn.cyc.ai.cog.platform.operations.support;

import cn.cyc.ai.cog.platform.operations.domain.MessageTemplate;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTemplateRendererTest {

    private final MessageTemplateRenderer renderer = new MessageTemplateRenderer(new com.fasterxml.jackson.databind.ObjectMapper());

    @Test
    void shouldRenderPlaceholders() {
        MessageTemplate template = new MessageTemplate(
                1L,
                "tpl.verify",
                "验证码",
                "SMS",
                "您的验证码是 {{code}}，请在 {{minutes}} 分钟内使用。",
                "[{\"name\":\"code\",\"required\":true},{\"name\":\"minutes\",\"required\":false}]",
                "ENABLED");

        String rendered = renderer.render(template, Map.of("code", "123456", "minutes", "5"));
        assertEquals("您的验证码是 123456，请在 5 分钟内使用。", rendered);
    }
}
