package cn.cyc.ai.cog.file.storage.spi;

import cn.cyc.ai.cog.file.storage.config.FileStorageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiskFileStorageStrategyTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldStoreReadAndDeleteOnDisk() {
        FileStorageProperties properties = new FileStorageProperties();
        properties.getDisk().setRootPath(tempDir.toString());
        DiskFileStorageStrategy strategy = new DiskFileStorageStrategy(properties);

        byte[] content = "hello-file-storage".getBytes(StandardCharsets.UTF_8);
        var stored = strategy.store(
                new ByteArrayInputStream(content),
                content.length,
                "demo.txt",
                "text/plain",
                1L,
                "test");

        assertEquals(content.length, stored.sizeBytes());
        assertArrayEquals(content, strategy.readBytes(stored.storagePath()));

        strategy.delete(stored.storagePath());
    }
}
