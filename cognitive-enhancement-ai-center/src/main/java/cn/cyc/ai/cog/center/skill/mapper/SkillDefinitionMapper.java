package cn.cyc.ai.cog.center.skill.mapper;

import cn.cyc.ai.cog.center.skill.entity.SkillDefinitionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 技能定义 Mapper。
 */
@Mapper
public interface SkillDefinitionMapper extends BaseMapper<SkillDefinitionEntity> {

    /**
     * 查询技能绑定的工具编码列表。
     */
    @Select("SELECT tool_code FROM qz_ai_skill_tool WHERE skill_code = #{skillCode}")
    List<String> selectToolCodes(@Param("skillCode") String skillCode);
}
