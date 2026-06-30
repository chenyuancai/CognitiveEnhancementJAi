package cn.cyc.ai.cog.file.storage.oss;

import cn.cyc.ai.cog.file.storage.domain.OssObject;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * 面向业务服务复用的对象存储模板。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface OssTemplate {

    OssObject putObject(String bucketName, String originalName, InputStream inputStream, long sizeBytes, String contentType);

    OssObject statObject(String bucketName, String objectName);

    byte[] readBytes(String bucketName, String objectName);

    Resource openAsResource(String bucketName, String objectName);

    void removeObject(String bucketName, String objectName);

    String objectPath(String bucketName, String objectName);

    String objectLink(String bucketName, String objectName);
}
