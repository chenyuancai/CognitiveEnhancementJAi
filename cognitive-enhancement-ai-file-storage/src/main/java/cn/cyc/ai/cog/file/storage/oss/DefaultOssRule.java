package cn.cyc.ai.cog.file.storage.oss;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 默认对象命名规则：按日期目录存储，文件名只保留扩展名。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class DefaultOssRule implements OssRule {

    /** DAYDIR。 */
    private static final DateTimeFormatter DAY_DIR = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 执行bucket名称。
     *
     * @param bucketName bucket名称
     * @return 执行结果
     */
    @Override
    public String bucketName(String bucketName) {
        return StringUtils.hasText(bucketName) ? bucketName.trim() : "default";
    }

    /**
     * 执行object名称。
     *
     * @param originalName original名称
     * @return 执行结果
     */
    @Override
    public String objectName(String originalName) {
        String extension = resolveExtension(originalName);
        return "upload/" + DAY_DIR.format(LocalDate.now()) + "/"
                + UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 执行resolveExtension。
     *
     * @param originalName original名称
     * @return 执行结果
     */
    private static String resolveExtension(String originalName) {
        if (!StringUtils.hasText(originalName)) {
            return "";
        }
        String name = originalName.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return "";
        }
        return "." + name.substring(dot + 1).replaceAll("[^a-zA-Z0-9]", "");
    }
}
