package cn.cyc.ai.cog.runtime.importkb.tool;

import cn.cyc.ai.cog.core.knowledge.process.model.KbContentChunk;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Markdown 分块工具。
 */
@Component
public class KbChunkBuildTool {

    private static final int MAX_CHUNK_CHARS = 900;

    public void buildChunks(ImportWorkflowState state) {
        state.getChunks().clear();
        String markdown = state.getMarkdown();
        if (!StringUtils.hasText(markdown)) {
            return;
        }
        List<String> sections = splitSections(markdown);
        int index = 0;
        for (String section : sections) {
            if (!StringUtils.hasText(section)) {
                continue;
            }
            String heading = extractHeading(section);
            for (String piece : splitBySize(section.trim(), MAX_CHUNK_CHARS)) {
                state.getChunks().add(new KbContentChunk(index++, piece, heading, List.of()));
            }
        }
        if (state.getChunks().isEmpty()) {
            state.getChunks().add(new KbContentChunk(0, markdown.trim(), "", List.of()));
        }
    }

    private List<String> splitSections(String markdown) {
        String[] parts = markdown.split("(?m)^#{1,3}\\s+");
        if (parts.length <= 1) {
            return List.of(markdown);
        }
        List<String> sections = new ArrayList<>();
        for (String part : parts) {
            if (StringUtils.hasText(part)) {
                sections.add(part);
            }
        }
        return sections;
    }

    private String extractHeading(String section) {
        int lineBreak = section.indexOf('\n');
        String firstLine = lineBreak < 0 ? section : section.substring(0, lineBreak);
        return firstLine.length() > 120 ? firstLine.substring(0, 120) : firstLine;
    }

    private List<String> splitBySize(String text, int max) {
        if (text.length() <= max) {
            return List.of(text);
        }
        List<String> pieces = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + max);
            if (end < text.length()) {
                int space = text.lastIndexOf('\n', end);
                if (space > start + max / 2) {
                    end = space;
                }
            }
            pieces.add(text.substring(start, end).trim());
            start = end;
        }
        return pieces;
    }
}
