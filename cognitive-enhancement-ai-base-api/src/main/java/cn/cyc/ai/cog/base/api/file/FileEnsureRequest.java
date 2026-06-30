package cn.cyc.ai.cog.base.api.file;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 批量确认文件（业务落库后调用，防止孤儿文件被清理）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class FileEnsureRequest {

    /** ids。 */
    @NotEmpty
    private List<Long> ids;

    /**
     * 获取Ids。
     * @return Ids
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * 设置Ids。
     *
     * @param ids ids
     */
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
