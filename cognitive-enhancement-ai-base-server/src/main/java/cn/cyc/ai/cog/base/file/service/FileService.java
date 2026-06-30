package cn.cyc.ai.cog.base.file.service;

import cn.cyc.ai.cog.base.api.file.FileEnsureRequest;
import cn.cyc.ai.cog.base.api.file.FileInfoDTO;
import cn.cyc.ai.cog.base.api.file.FilePageQuery;
import cn.cyc.ai.cog.base.api.file.FileUploadBytesRequest;
import cn.cyc.ai.cog.base.config.BaseServiceProperties;
import cn.cyc.ai.cog.base.file.entity.FileEntity;
import cn.cyc.ai.cog.base.file.enums.FileStatusEnum;
import cn.cyc.ai.cog.base.file.repository.FileRepository;
import cn.cyc.ai.cog.base.file.support.FileConverter;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.file.storage.config.FileStorageProperties;
import cn.cyc.ai.cog.file.storage.domain.StoredFileObject;
import cn.cyc.ai.cog.file.storage.spi.FileStorageStrategy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

/**
 * 文件元数据编排：二进制存储委托 {@link FileStorageStrategy}（file-storage 基础组件）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class FileService {

    /** 文件仓储。 */
    private final FileRepository fileRepository;
    /** 文件Storage策略。 */
    private final FileStorageStrategy fileStorageStrategy;
    /** 文件StorageProperties。 */
    private final FileStorageProperties fileStorageProperties;
    /** base服务Properties。 */
    private final BaseServiceProperties baseServiceProperties;

    /**
     * 创建文件服务。
     */
    public FileService(FileRepository fileRepository,
                       FileStorageStrategy fileStorageStrategy,
                       FileStorageProperties fileStorageProperties,
                       BaseServiceProperties baseServiceProperties) {
        this.fileRepository = fileRepository;
        this.fileStorageStrategy = fileStorageStrategy;
        this.fileStorageProperties = fileStorageProperties;
        this.baseServiceProperties = baseServiceProperties;
    }

    /**
     * 执行upload。
     *
     * @param file 文件
     * @param tenantId 租户 ID
     * @param bizCode biz编码
     * @return 执行结果
     */
    public FileInfoDTO upload(MultipartFile file, Long tenantId, String bizCode) {
        if (file == null || file.isEmpty()) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "上传文件不能为空");
        }
        try {
            return persistUpload(
                    file.getInputStream(),
                    file.getSize(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    tenantId,
                    bizCode);
        } catch (IOException exception) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "读取上传流失败");
        }
    }

    /**
     * 执行uploadBytes。
     *
     * @param request 请求
     * @return 执行结果
     */
    public FileInfoDTO uploadBytes(FileUploadBytesRequest request) {
        byte[] bytes = Base64.getDecoder().decode(request.getBase64Content());
        String contentType = StringUtils.hasText(request.getContentType())
                ? request.getContentType()
                : "application/octet-stream";
        return persistUpload(
                new ByteArrayInputStream(bytes),
                bytes.length,
                request.getFileName(),
                contentType,
                request.getTenantId(),
                request.getBizCode());
    }

    /**
     * 获取人ID。
     *
     * @param id 主键 ID
     * @return 人ID
     */
    public FileInfoDTO getById(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在"));
        return FileConverter.toDto(entity);
    }

    /**
     * 查询人Ids列表。
     *
     * @param ids ids
     * @return 结果列表
     */
    public List<FileInfoDTO> listByIds(List<Long> ids) {
        return fileRepository.findByIds(ids).stream().map(FileConverter::toDto).toList();
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<FileInfoDTO> page(FilePageQuery query) {
        return fileRepository.page(query).map(FileConverter::toDto);
    }

    /**
     * 执行ensure。
     *
     * @param request 请求
     */
    public void ensure(FileEnsureRequest request) {
        fileRepository.updateStatus(request.getIds(), FileStatusEnum.CONFIRMED.getCode());
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public void delete(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在"));
        fileStorageStrategy.delete(entity.getStoragePath());
        fileRepository.deleteById(id);
    }

    /**
     * 执行loadAsResource。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public Resource loadAsResource(Long id) {
        return fileStorageStrategy.openAsResource(requireStoragePath(id));
    }

    /**
     * 执行readBytes。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public byte[] readBytes(Long id) {
        return fileStorageStrategy.readBytes(requireStoragePath(id));
    }

    /**
     * 执行resolveOriginal名称。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public String resolveOriginalName(Long id) {
        return fileRepository.findById(id)
                .map(FileEntity::getOriginalName)
                .orElse("file");
    }

    /**
     * 执行resolve内容类型。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public String resolveContentType(Long id) {
        return fileRepository.findById(id)
                .map(FileEntity::getContentType)
                .orElse("application/octet-stream");
    }

    /**
     * 执行requireStorage路径。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    private String requireStoragePath(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在"));
        return entity.getStoragePath();
    }

    /**
     * 执行persistUpload。
     * @return 执行结果
     */
    private FileInfoDTO persistUpload(InputStream inputStream,
                                      long sizeBytes,
                                      String originalName,
                                      String contentType,
                                      Long tenantId,
                                      String bizCode) {
        if (sizeBytes > fileStorageProperties.resolveMaxFileSize()) {
            throw Errors.of(PlatformErrorCode.BAD_REQUEST, "文件大小超过限制");
        }
        Long resolvedTenantId = tenantId == null ? 1L : tenantId;
        String resolvedBizCode = StringUtils.hasText(bizCode)
                ? bizCode.trim()
                : baseServiceProperties.getDefaultBizCode();
        StoredFileObject stored = fileStorageStrategy.store(
                inputStream,
                sizeBytes,
                originalName,
                contentType,
                resolvedTenantId,
                resolvedBizCode);
        FileEntity entity = new FileEntity();
        entity.setTenantId(resolvedTenantId);
        entity.setBizCode(resolvedBizCode);
        entity.setOriginalName(StringUtils.hasText(originalName) ? originalName : "file");
        entity.setStorageName(stored.storageName());
        entity.setStoragePath(stored.storagePath());
        entity.setContentType(StringUtils.hasText(contentType) ? contentType : "application/octet-stream");
        entity.setSizeBytes(stored.sizeBytes());
        entity.setMd5(stored.md5());
        entity.setStatus(FileStatusEnum.UNCONFIRMED.getCode());
        fileRepository.save(entity);
        return FileConverter.toDto(entity);
    }
}
