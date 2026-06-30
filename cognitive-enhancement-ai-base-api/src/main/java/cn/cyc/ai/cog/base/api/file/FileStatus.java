package cn.cyc.ai.cog.base.api.file;

/**
 * 文件确认状态（与 base-server 持久化一致）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum FileStatus {

    /** 已上传未确认，可被清理任务回收 */
    UNCONFIRMED,

    /** 业务已确认，长期保留 */
    CONFIRMED
}
