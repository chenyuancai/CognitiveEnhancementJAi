package cn.cyc.ai.cog.base.file.support;

import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.base.api.file.FileStatus;
import cn.cyc.ai.cog.base.file.entity.FileEntity;
import cn.cyc.ai.cog.base.file.enums.FileStatusEnum;

/**
 * 文件 Entity ↔ API DTO 转换。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class FileConverter {

    /** PUBLICDOWNLOADPREFIX。 */
    private static final String PUBLIC_DOWNLOAD_PREFIX = "/api/base/files/";

    /**
     * 创建FileConverter。
     */
    private FileConverter() {
    }

    /**
     * 转换为Dto。
     *
     * @param entity 实体
     * @return 转换结果
     */
    public static FileInfoDTO toDto(FileEntity entity) {
        FileInfoDTO dto = new FileInfoDTO();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        dto.setBizCode(entity.getBizCode());
        dto.setOriginalName(entity.getOriginalName());
        dto.setContentType(entity.getContentType());
        dto.setSizeBytes(entity.getSizeBytes());
        dto.setMd5(entity.getMd5());
        dto.setStoragePath(entity.getStoragePath());
        dto.setAccessPath(PUBLIC_DOWNLOAD_PREFIX + entity.getId() + "/download");
        dto.setStatus(toApiStatus(entity.getStatus()));
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    /**
     * 转换为Api状态。
     *
     * @param statusCode 状态编码
     * @return 转换结果
     */
    public static FileStatus toApiStatus(Integer statusCode) {
        if (statusCode == null) {
            return FileStatus.UNCONFIRMED;
        }
        return FileStatusEnum.fromCode(statusCode) == FileStatusEnum.CONFIRMED
                ? FileStatus.CONFIRMED
                : FileStatus.UNCONFIRMED;
    }

    /**
     * 转换为Db状态。
     *
     * @param status 状态
     * @return 转换结果
     */
    public static Integer toDbStatus(FileStatus status) {
        if (status == FileStatus.CONFIRMED) {
            return FileStatusEnum.CONFIRMED.getCode();
        }
        return FileStatusEnum.UNCONFIRMED.getCode();
    }
}
