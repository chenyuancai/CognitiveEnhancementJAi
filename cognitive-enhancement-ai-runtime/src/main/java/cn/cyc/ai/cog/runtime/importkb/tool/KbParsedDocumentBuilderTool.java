package cn.cyc.ai.cog.runtime.importkb.tool;

import cn.cyc.ai.cog.core.knowledge.process.model.KbImageRef;
import cn.cyc.ai.cog.core.knowledge.process.model.KbParsedDocument;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 由 Markdown 构建结构化解析文档。
 */
@Component
public class KbParsedDocumentBuilderTool {

    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[(.*?)]\\((.*?)\\)");

    public void build(ImportWorkflowState state) {
        String markdown = state.getMarkdown();
        String title = StringUtils.hasText(state.getTitle()) ? state.getTitle() : state.getFileName();
        List<KbImageRef> images = extractImages(markdown);
        String plain = markdown == null ? "" : markdown.replaceAll("[#>*`_\\-]", " ").replaceAll("\\s+", " ").trim();
        state.setParsedDocument(new KbParsedDocument(
                title == null ? "导入内容" : title,
                markdown == null ? "" : markdown,
                plain,
                images));
    }

    private List<KbImageRef> extractImages(String markdown) {
        List<KbImageRef> images = new ArrayList<>();
        if (!StringUtils.hasText(markdown)) {
            return images;
        }
        Matcher matcher = IMAGE_PATTERN.matcher(markdown);
        while (matcher.find()) {
            images.add(new KbImageRef(matcher.group(1), matcher.group(2), null));
        }
        return images;
    }
}
