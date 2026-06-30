package cn.cyc.ai.cog.platform.knowledge.support;

import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 内容 CSV 导入解析：固定表头 title,content_type,author,summary,body,min_level_code,tags。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class ContentImportCsvSupport {

    private static final List<String> EXPECTED_HEADERS = List.of(
            "title", "content_type", "author", "summary", "body", "min_level_code", "tags");

    /**
     * 创建内容ImportCsv支持工具。
     */
    private ContentImportCsvSupport() {
    }

    /**
     * 执行parse。
     *
     * @param csvContent csv内容
     * @return 执行结果
     */
    public static ParseResult parse(String csvContent) {
        if (!StringUtils.hasText(csvContent)) {
            throw new IllegalArgumentException("CSV 内容为空");
        }
        String[] lines = csvContent.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("CSV 至少包含表头与一行数据");
        }
        List<String> headers = splitCsvLine(lines[0]);
        validateHeaders(headers);
        List<RowResult> rows = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (!StringUtils.hasText(lines[i])) {
                continue;
            }
            int lineNo = i + 1;
            try {
                List<String> cells = splitCsvLine(lines[i]);
                Map<String, String> row = mapRow(headers, cells);
                String title = required(row, "title", lineNo);
                String contentType = required(row, "content_type", lineNo);
                ContentSaveRequest request = new ContentSaveRequest();
                request.setTitle(title);
                request.setType(contentType);
                request.setAuthor(row.get("author"));
                request.setSummary(row.get("summary"));
                request.setBody(row.get("body"));
                request.setMinLevelCode(blankToNull(row.get("min_level_code")));
                rows.add(new RowResult(lineNo, request, row.get("tags"), null));
            } catch (Exception ex) {
                rows.add(new RowResult(lineNo, null, null, ex.getMessage()));
            }
        }
        return new ParseResult(rows);
    }

    /**
     * 校验参数。
     *
     * @param headers headers
     */
    private static void validateHeaders(List<String> headers) {
        if (headers.size() < EXPECTED_HEADERS.size()) {
            throw new IllegalArgumentException("CSV 表头列不足，期望：" + EXPECTED_HEADERS);
        }
        for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
            String expected = EXPECTED_HEADERS.get(i);
            String actual = headers.get(i).trim().toLowerCase(Locale.ROOT);
            if (!expected.equals(actual)) {
                throw new IllegalArgumentException("CSV 表头第 " + (i + 1) + " 列应为 " + expected + "，实际为 " + actual);
            }
        }
    }

    private static Map<String, String> mapRow(List<String> headers, List<String> cells) {
        Map<String, String> row = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String key = headers.get(i).trim().toLowerCase(Locale.ROOT);
            String value = i < cells.size() ? cells.get(i) : "";
            row.put(key, value == null ? "" : value.trim());
        }
        return row;
    }

    /**
     * 执行required。
     *
     * @param row row
     * @param key 键
     * @param lineNo lineNo
     * @return 执行结果
     */
    private static String required(Map<String, String> row, String key, int lineNo) {
        String value = row.get(key);
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("第 " + lineNo + " 行缺少必填列：" + key);
        }
        return value.trim();
    }

    /**
     * 执行blankToNull。
     *
     * @param value 值
     * @return 执行结果
     */
    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    static List<String> splitCsvLine(String line) {
        List<String> cells = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                cells.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        cells.add(current.toString());
        return cells;
    }

    /**
     * Row结果
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record RowResult(int lineNo, ContentSaveRequest request, String tags, String error) {
        /**
         * 构建成功响应。
         * @return 统一响应对象
         */
        public boolean success() {
            return error == null && request != null;
        }
    }

    /**
     * Parse结果
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record ParseResult(List<RowResult> rows) {

        /**
         * 执行成功数量。
         * @return 执行结果
         */
        public int successCount() {
            return (int) rows.stream().filter(RowResult::success).count();
        }

        /**
         * 执行fail数量。
         * @return 执行结果
         */
        public int failCount() {
            return rows.size() - successCount();
        }

        /**
         * 转换为结果JSON。
         * @return 转换结果
         */
        public String toResultJson() {
            StringBuilder sb = new StringBuilder("{\"rows\":[");
            for (int i = 0; i < rows.size(); i++) {
                RowResult row = rows.get(i);
                if (i > 0) {
                    sb.append(',');
                }
                sb.append("{\"lineNo\":").append(row.lineNo());
                if (row.success()) {
                    sb.append(",\"success\":true,\"title\":\"").append(escape(row.request().getTitle())).append("\"");
                } else {
                    sb.append(",\"success\":false,\"error\":\"").append(escape(row.error())).append("\"");
                }
                sb.append('}');
            }
            sb.append("]}");
            return sb.toString();
        }

        /**
         * 执行escape。
         *
         * @param value 值
         * @return 执行结果
         */
        private static String escape(String value) {
            if (value == null) {
                return "";
            }
            return value.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
