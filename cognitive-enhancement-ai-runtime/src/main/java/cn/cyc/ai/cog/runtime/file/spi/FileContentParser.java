package cn.cyc.ai.cog.runtime.file.spi;

import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;

/**
 * 文件正文解析器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface FileContentParser {

    /**
     * 解析上传文件正文。
     *
     * @param upload 上传记录
     * @return 文件正文
     */
    String parseText(FileUploadRecord upload);
}
