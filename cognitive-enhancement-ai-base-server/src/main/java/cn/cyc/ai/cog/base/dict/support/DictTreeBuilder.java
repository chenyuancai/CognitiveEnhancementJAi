package cn.cyc.ai.cog.base.dict.support;

import cn.cyc.ai.cog.base.dict.dto.DictItemVO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典项树构建工具。
 */
public final class DictTreeBuilder {

    private DictTreeBuilder() {
    }

    /**
     * 将扁平列表按 parentId 组装为树。
     */
    public static List<DictItemVO> buildTree(List<DictItemVO> flat, long rootParentId) {
        Map<Long, DictItemVO> index = new HashMap<>();
        for (DictItemVO item : flat) {
            item.setChildren(new ArrayList<>());
            index.put(item.getId(), item);
        }
        List<DictItemVO> roots = new ArrayList<>();
        for (DictItemVO item : flat) {
            Long parentId = item.getParentId() == null ? rootParentId : item.getParentId();
            if (parentId == null || parentId == rootParentId) {
                roots.add(item);
                continue;
            }
            DictItemVO parent = index.get(parentId);
            if (parent == null) {
                roots.add(item);
            } else {
                parent.getChildren().add(item);
            }
        }
        sortTree(roots);
        return roots;
    }

    private static void sortTree(List<DictItemVO> nodes) {
        nodes.sort(Comparator.comparing(DictItemVO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(DictItemVO::getId, Comparator.nullsLast(Long::compareTo)));
        for (DictItemVO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }
}
