package cn.cyc.ai.cog.center.prompt.mapper;

import cn.cyc.ai.cog.center.prompt.entity.PromptTemplateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提示词模板 Mapper。
 */
@Mapper
public interface PromptTemplateMapper extends BaseMapper<PromptTemplateEntity> {
}
