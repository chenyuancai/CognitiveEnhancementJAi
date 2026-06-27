package cn.cyc.ai.cog.center.agent.mapper;

import cn.cyc.ai.cog.center.agent.entity.AgentDefinitionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Agent 定义 Mapper。
 */
@Mapper
public interface AgentDefinitionMapper extends BaseMapper<AgentDefinitionEntity> {

    /**
     * 查询 Agent 绑定的技能编码列表。
     */
    @Select("SELECT skill_code FROM qz_ai_agent_skill WHERE agent_code = #{agentCode}")
    List<String> selectSkillCodes(@Param("agentCode") String agentCode);
}
