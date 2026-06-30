package cn.cyc.ai.cog.runtime.importkb.tool;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * HTML 转 Markdown（轻量标签剥离，保留段落结构）。
 */
@Component
public class KbHtmlToMarkdownTool {

    public void toMarkdown(ImportWorkflowState state) {
        String html = state.getHtml();
        if (!StringUtils.hasText(html)) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "HTML 为空");
        }
        String markdown = html
                .replaceAll("(?is)<h1[^>]*>(.*?)</h1>", "# $1\n\n")
                .replaceAll("(?is)<h2[^>]*>(.*?)</h2>", "## $1\n\n")
                .replaceAll("(?is)<h3[^>]*>(.*?)</h3>", "### $1\n\n")
                .replaceAll("(?is)<p[^>]*>(.*?)</p>", "$1\n\n")
                .replaceAll("(?is)<pre[^>]*>(.*?)</pre>", "```\n$1\n```\n\n")
                .replaceAll("(?is)<br\\s*/?>", "\n")
                .replaceAll("(?is)<[^>]+>", "")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
        state.setMarkdown(markdown);
    }
}
