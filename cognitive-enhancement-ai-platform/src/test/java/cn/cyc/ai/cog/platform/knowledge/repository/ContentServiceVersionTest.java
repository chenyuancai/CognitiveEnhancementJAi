package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.platform.knowledge.dto.ContentAuditRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentRollbackRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import cn.cyc.ai.cog.api.enums.ContentStatus;
import cn.cyc.ai.cog.platform.knowledge.service.ContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceVersionTest {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    @Test
    void shouldListVersions() {
        when(contentRepository.listVersions(1L)).thenReturn(List.of(
                new ContentVersion(1L, 1L, 1, "v1", "s", "b", "FREE", 9L, LocalDateTime.now())
        ));

        List<ContentVersion> versions = contentService.listVersions(1L);
        assertEquals(1, versions.size());
        assertEquals(1, versions.get(0).versionNo());
    }

    @Test
    void shouldRollbackToDraft() {
        ContentRollbackRequest request = new ContentRollbackRequest();
        request.setVersionNo(1);
        when(contentRepository.rollbackToVersion(1L, 1)).thenReturn(
                new Content(1L, "v1", "ARTICLE", "a", ContentStatus.DRAFT.code(), "s", "b", null, "FREE", 1, null)
        );

        Content content = contentService.rollback(1L, request);
        assertEquals(ContentStatus.DRAFT.code(), content.status());
        verify(contentRepository).rollbackToVersion(eq(1L), eq(1));
    }
}
