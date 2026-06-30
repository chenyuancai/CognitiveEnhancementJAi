package cn.cyc.ai.cog.runtime.file.spi;

import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;

/**
 * 文件正文读取器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface FileContentLoader {

    boolean supports(FileUploadRecord upload);

    String readText(FileUploadRecord upload);
}
