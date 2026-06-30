package cn.cyc.ai.cog.runtime.file.service;

import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.repository.InMemoryFileParseTaskRepository;
import cn.cyc.ai.cog.runtime.file.repository.InMemoryFileUploadRecordRepository;
import cn.cyc.ai.cog.runtime.file.spi.FileContentParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FileProcessingServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldParseTextFileContentInsteadOfMockPreview() throws Exception {
        Path file = tempDir.resolve("lesson.txt");
        Files.writeString(file, "第一段学习材料\n第二段重点内容", StandardCharsets.UTF_8);
        FileProcessingService service = new FileProcessingService(
                new InMemoryFileUploadRecordRepository(),
                new InMemoryFileParseTaskRepository(),
                new DefaultFileContentParser(),
                new ObjectMapper()
        );
        var upload = service.registerUpload(
                "lesson.txt",
                "text/plain",
                Files.size(file),
                file.toString(),
                "sha256-demo"
        );

        FileParseTask task = service.startParse(upload.fileId());

        JsonNode result = new ObjectMapper().readTree(task.parseResult());
        assertEquals("第一段学习材料\n第二段重点内容", result.get("text").asText());
        assertFalse(result.get("textPreview").asText().contains("mock parsed"));
    }

    @Test
    void shouldParseTextFileFromBaseFileReference() throws Exception {
        FileContentParser parser = upload -> "来自文件中心的内容";
        FileProcessingService service = new FileProcessingService(
                new InMemoryFileUploadRecordRepository(),
                new InMemoryFileParseTaskRepository(),
                parser,
                new ObjectMapper()
        );
        var upload = service.registerUpload(
                "lesson.txt",
                "text/plain",
                21,
                "base:42",
                "md5-demo"
        );

        FileParseTask task = service.startParse(upload.fileId());

        JsonNode result = new ObjectMapper().readTree(task.parseResult());
        assertEquals("来自文件中心的内容", result.get("text").asText());
    }
}
