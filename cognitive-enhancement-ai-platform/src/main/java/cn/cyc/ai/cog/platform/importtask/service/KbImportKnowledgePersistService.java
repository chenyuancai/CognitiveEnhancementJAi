package cn.cyc.ai.cog.platform.importtask.service;

import cn.cyc.ai.cog.core.knowledge.process.model.KbContentChunk;
import cn.cyc.ai.cog.core.knowledge.process.spi.ImportKnowledgePersistPort;
import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.entity.KbContentChunkEntity;
import cn.cyc.ai.cog.platform.knowledge.entity.KbVectorIndexRecordEntity;
import cn.cyc.ai.cog.platform.knowledge.mapper.KbContentChunkMapper;
import cn.cyc.ai.cog.platform.knowledge.mapper.KbVectorIndexRecordMapper;
import cn.cyc.ai.cog.platform.knowledge.repository.ContentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入工作流落库：草稿内容 + 分块 + 向量索引。
 */
@Service
public class KbImportKnowledgePersistService implements ImportKnowledgePersistPort {

    private final ContentRepository contentRepository;
    private final KbContentChunkMapper chunkMapper;
    private final KbVectorIndexRecordMapper vectorMapper;
    private final ObjectMapper objectMapper;

    public KbImportKnowledgePersistService(ContentRepository contentRepository,
                                           KbContentChunkMapper chunkMapper,
                                           KbVectorIndexRecordMapper vectorMapper,
                                           ObjectMapper objectMapper) {
        this.contentRepository = contentRepository;
        this.chunkMapper = chunkMapper;
        this.vectorMapper = vectorMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Long persist(ImportWorkflowState state) {
        ContentSaveRequest request = new ContentSaveRequest();
        String title = StringUtils.hasText(state.getTitle()) ? state.getTitle() : state.getFileName();
        request.setTitle(title != null ? title : "导入内容");
        request.setType("ARTICLE");
        request.setBody(state.getMarkdown());
        request.setSummary(state.getSummary());
        var content = contentRepository.createImportDraft(state.getTenantId(), request);
        Long contentId = content.id();
        int index = 0;
        for (KbContentChunk chunk : state.getChunks()) {
            KbContentChunkEntity entity = new KbContentChunkEntity();
            entity.setTenantId(state.getTenantId());
            entity.setContentId(contentId);
            entity.setTaskCode(state.getTaskCode());
            entity.setChunkIndex(chunk.chunkIndex() >= 0 ? chunk.chunkIndex() : index);
            entity.setHeadingPath(chunk.headingPath());
            entity.setChunkText(chunk.chunkText());
            entity.setTokenEst(estimateTokens(chunk.chunkText()));
            entity.setCreateTime(LocalDateTime.now());
            chunkMapper.insert(entity);
            if (chunk.embedding() != null && !chunk.embedding().isEmpty()) {
                KbVectorIndexRecordEntity vector = new KbVectorIndexRecordEntity();
                vector.setTenantId(state.getTenantId());
                vector.setContentId(contentId);
                vector.setChunkId(entity.getId());
                vector.setModelCode("import.embedding");
                vector.setDim(chunk.embedding().size());
                vector.setVectorJson(writeVector(chunk.embedding()));
                vector.setCreateTime(LocalDateTime.now());
                vectorMapper.insert(vector);
            }
            index++;
        }
        return contentId;
    }

    private int estimateTokens(String text) {
        if (!StringUtils.hasText(text)) {
            return 0;
        }
        return Math.max(1, text.length() / 4);
    }

    private String writeVector(List<Float> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception ex) {
            return "[]";
        }
    }
}
