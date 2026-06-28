package cn.cyc.ai.cog.platform.knowledge.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentImportCsvSupportTest {

    @Test
    void shouldParseValidCsvRows() {
        String csv = """
                title,content_type,author,summary,body,min_level_code,tags
                文章A,ARTICLE,作者A,摘要A,正文A,FREE,tag1
                ,ARTICLE,作者B,摘要B,正文B,,
                """;
        ContentImportCsvSupport.ParseResult result = ContentImportCsvSupport.parse(csv);
        assertEquals(2, result.rows().size());
        assertEquals(1, result.successCount());
        assertEquals(1, result.failCount());
        assertTrue(result.rows().get(0).success());
        assertFalse(result.rows().get(1).success());
    }

    @Test
    void shouldRejectInvalidHeader() {
        String csv = """
                title,type,author,summary,body,min_level_code,tags
                文章A,ARTICLE,作者A,摘要A,正文A,FREE,
                """;
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ContentImportCsvSupport.parse(csv));
    }
}
