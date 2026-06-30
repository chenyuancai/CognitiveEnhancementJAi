package cn.cyc.ai.cog.file.storage.oss;

/**
 * 对象存储命名规则。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface OssRule {

    String bucketName(String bucketName);

    String objectName(String originalName);
}
