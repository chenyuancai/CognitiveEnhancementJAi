package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.entity.ContentTagEntity;
import cn.cyc.ai.cog.platform.knowledge.mapper.ContentTagMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 内容标签仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbContentTagRepository implements ContentTagRepository {

    /** 内容标签 Mapper */
    private final ContentTagMapper contentTagMapper;

    /**
     * @param contentTagMapper 内容标签 Mapper
     */
    public DbContentTagRepository(ContentTagMapper contentTagMapper) {
        this.contentTagMapper = contentTagMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<ContentTag> page(ContentTagPageQuery query) {
        LambdaQueryWrapper<ContentTagEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(ContentTagEntity::getTagName, query.getKeyword());
        }
        wrapper.orderByDesc(ContentTagEntity::getId);
        Page<ContentTagEntity> page = contentTagMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public ContentTag create(ContentTagSaveRequest request) {
        ContentTagEntity tag = new ContentTagEntity();
        tag.setTagName(request.getTagName().trim());
        tag.setTagColor(request.getTagColor());
        contentTagMapper.insert(tag);
        return toDomain(tag);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public ContentTag update(Long id, ContentTagSaveRequest request) {
        ContentTagEntity tag = require(id);
        tag.setTagName(request.getTagName().trim());
        tag.setTagColor(request.getTagColor());
        contentTagMapper.updateById(tag);
        return toDomain(tag);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    @Override
    public void delete(Long id) {
        require(id);
        contentTagMapper.deleteById(id);
    }

    /**
     * 执行require。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    private ContentTagEntity require(Long id) {
        ContentTagEntity tag = contentTagMapper.selectById(id);
        if (tag == null) {
            throw Errors.of(PlatformErrorCode.CONTENT_TAG_NOT_FOUND);
        }
        return tag;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private ContentTag toDomain(ContentTagEntity entity) {
        return new ContentTag(entity.getId(), entity.getTagName(), entity.getTagColor());
    }
}
