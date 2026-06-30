package cn.cyc.ai.cog.core.harness;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;

import java.util.List;

/**
 * 技能装载器，负责将 Agent 绑定的技能编码解析为可执行的技能定义列表。
 * <p>一期的职责：
 * <ul>
 * <li>按编码从仓储查询技能定义</li>
 * <li>验证技能状态（ENABLED）</li>
 * <li>验证技能与 Agent 的绑定权限</li>
 * <li>按加载顺序排序</li>
 * </ul>
 * <p>二期可扩展：技能版本管理、动态技能发现、技能依赖解析。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface SkillLoader {

    /**
     * 为指定 Agent 加载其绑定的全部可用技能。
     *
     * @param agent 智能体定义
     * @return 排序后的技能定义列表，空列表表示该 Agent 无绑定技能
     */
    List<SkillDefinition> loadForAgent(AgentDefinition agent);

    /**
     * 按编码列表批量加载技能定义。
     *
     * @param skillCodes 技能编码列表
     * @return 技能定义列表，不存在的编码被忽略
     */
    List<SkillDefinition> loadByCodes(List<String> skillCodes);
}
