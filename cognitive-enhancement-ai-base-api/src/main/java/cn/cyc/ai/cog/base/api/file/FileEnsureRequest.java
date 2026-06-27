package cn.cyc.ai.cog.base.api.file;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 批量确认文件（业务落库后调用，防止孤儿文件被清理）。
 */
public class FileEnsureRequest {

    @NotEmpty
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
