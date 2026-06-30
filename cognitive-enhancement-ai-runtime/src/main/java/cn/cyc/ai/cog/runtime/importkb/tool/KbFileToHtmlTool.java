package cn.cyc.ai.cog.runtime.importkb.tool;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件转 HTML（文本/Markdown 包装，PDF 抽取文本段落）。
 */
@Component
public class KbFileToHtmlTool {

    public void toHtml(ImportWorkflowState state) {
        Path path = state.getLocalFilePath();
        if (path == null) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "本地文件未解析");
        }
        String lower = path.getFileName().toString().toLowerCase();
        try {
            if (lower.endsWith(".pdf")) {
                state.setHtml(pdfToHtml(path));
                return;
            }
            String text = Files.readString(path, StandardCharsets.UTF_8);
            if (lower.endsWith(".md") || lower.endsWith(".markdown")) {
                state.setHtml("<article class=\"markdown\">" + escapeHtml(text) + "</article>");
            } else {
                state.setHtml("<pre>" + escapeHtml(text) + "</pre>");
            }
        } catch (IOException ex) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取文件失败");
        }
    }

    private String pdfToHtml(Path path) throws IOException {
        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (!StringUtils.hasText(text)) {
                return "<p>（PDF 未抽取到文本）</p>";
            }
            String[] paragraphs = text.split("\\R{2,}");
            StringBuilder html = new StringBuilder("<article>");
            for (String paragraph : paragraphs) {
                if (StringUtils.hasText(paragraph)) {
                    html.append("<p>").append(escapeHtml(paragraph.trim())).append("</p>");
                }
            }
            html.append("</article>");
            return html.toString();
        }
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
