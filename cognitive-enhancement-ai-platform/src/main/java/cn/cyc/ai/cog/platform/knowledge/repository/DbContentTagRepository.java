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

    @Override
    public ContentTag create(ContentTagSaveRequest request) {
        ContentTagEntity tag = new ContentTagEntity();
        tag.setTagName(request.getTagName().trim());
        tag.setTagColor(request.getTagColor());
        contentTagMapper.insert(tag);
        return toDomain(tag);
    }

    @Override
    public ContentTag update(Long id, ContentTagSaveRequest request) {
        ContentTagEntity tag = require(id);
        tag.setTagName(request.getTagName().trim());
        tag.setTagColor(request.getTagColor());
        contentTagMapper.updateById(tag);
        return toDomain(tag);
    }

    @Override
    public void delete(Long id) {
        require(id);
        contentTagMapper.deleteById(id);
    }

    private ContentTagEntity require(Long id) {
        ContentTagEntity tag = contentTagMapper.selectById(id);
        if (tag == null) {
            throw Errors.of(PlatformErrorCode.CONTENT_TAG_NOT_FOUND);
        }
        return tag;
    }

    private ContentTag toDomain(ContentTagEntity entity) {
        return new ContentTag(entity.getId(), entity.getTagName(), entity.getTagColor());
    }
}
