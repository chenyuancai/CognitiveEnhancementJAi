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
 */
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FileStorageStrategy fileStorageStrategy;
    private final FileStorageProperties fileStorageProperties;
    private final BaseServiceProperties baseServiceProperties;

    public FileService(FileRepository fileRepository,
                       FileStorageStrategy fileStorageStrategy,
                       FileStorageProperties fileStorageProperties,
                       BaseServiceProperties baseServiceProperties) {
        this.fileRepository = fileRepository;
        this.fileStorageStrategy = fileStorageStrategy;
        this.fileStorageProperties = fileStorageProperties;
        this.baseServiceProperties = baseServiceProperties;
    }

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

    public FileInfoDTO getById(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在"));
        return FileConverter.toDto(entity);
    }

    public List<FileInfoDTO> listByIds(List<Long> ids) {
        return fileRepository.findByIds(ids).stream().map(FileConverter::toDto).toList();
    }

    public PageResult<FileInfoDTO> page(FilePageQuery query) {
        return fileRepository.page(query).map(FileConverter::toDto);
    }

    public void ensure(FileEnsureRequest request) {
        fileRepository.updateStatus(request.getIds(), FileStatusEnum.CONFIRMED.getCode());
    }

    public void delete(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在"));
        fileStorageStrategy.delete(entity.getStoragePath());
        fileRepository.deleteById(id);
    }

    public Resource loadAsResource(Long id) {
        return fileStorageStrategy.openAsResource(requireStoragePath(id));
    }

    public byte[] readBytes(Long id) {
        return fileStorageStrategy.readBytes(requireStoragePath(id));
    }

    public String resolveOriginalName(Long id) {
        return fileRepository.findById(id)
                .map(FileEntity::getOriginalName)
                .orElse("file");
    }

    public String resolveContentType(Long id) {
        return fileRepository.findById(id)
                .map(FileEntity::getContentType)
                .orElse("application/octet-stream");
    }

    private String requireStoragePath(Long id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在"));
        return entity.getStoragePath();
    }

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
