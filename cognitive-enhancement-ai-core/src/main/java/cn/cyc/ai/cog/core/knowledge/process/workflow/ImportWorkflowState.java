package cn.cyc.ai.cog.core.knowledge.process.workflow;

import cn.cyc.ai.cog.core.knowledge.process.ImportBizType;
import cn.cyc.ai.cog.core.knowledge.process.model.KbContentChunk;
import cn.cyc.ai.cog.core.knowledge.process.model.KbParsedDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入工作流可变上下文（跨节点共享）。
 */
public class ImportWorkflowState {

    private Long tenantId;
    private Long userId;
    private String taskCode;
    private ImportBizType importBizType;
    private String channel;
    private Long fileId;
    private String fileUrl;
    private String fileName;
    private String title;
    private boolean aiEnhanced;
    private boolean autoQuiz;

    private Path localFilePath;
    private String contentType;
    private String html;
    private String markdown;
    private KbParsedDocument parsedDocument;
    private final List<KbContentChunk> chunks = new ArrayList<>();
    private Long contentId;
    private String summary;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public ImportBizType getImportBizType() {
        return importBizType;
    }

    public void setImportBizType(ImportBizType importBizType) {
        this.importBizType = importBizType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAiEnhanced() {
        return aiEnhanced;
    }

    public void setAiEnhanced(boolean aiEnhanced) {
        this.aiEnhanced = aiEnhanced;
    }

    public boolean isAutoQuiz() {
        return autoQuiz;
    }

    public void setAutoQuiz(boolean autoQuiz) {
        this.autoQuiz = autoQuiz;
    }

    public Path getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(Path localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public KbParsedDocument getParsedDocument() {
        return parsedDocument;
    }

    public void setParsedDocument(KbParsedDocument parsedDocument) {
        this.parsedDocument = parsedDocument;
    }

    public List<KbContentChunk> getChunks() {
        return chunks;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
