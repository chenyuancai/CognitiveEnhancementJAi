package cn.cyc.ai.cog.runtime.harness.spi;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Harness 执行取消令牌，支持 WebSocket CANCEL 中断后续步骤。
 *
 * @author cyc
 */
public class HarnessCancellation {

    private static final HarnessCancellation NONE = new HarnessCancellation(false);

    private final boolean mutable;
    private final AtomicBoolean cancelled;

    private HarnessCancellation(boolean mutable) {
        this.mutable = mutable;
        this.cancelled = new AtomicBoolean(false);
    }

    /**
     * 创建可变的取消令牌。
     *
     * @return 取消令牌
     */
    public static HarnessCancellation create() {
        return new HarnessCancellation(true);
    }

    /**
     * 返回永不取消的占位令牌。
     *
     * @return 占位令牌
     */
    public static HarnessCancellation none() {
        return NONE;
    }

    /**
     * 标记为已取消。
     */
    public void cancel() {
        if (mutable) {
            cancelled.set(true);
        }
    }

    /**
     * 是否已取消。
     *
     * @return 是否已取消
     */
    public boolean isCancelled() {
        return cancelled.get();
    }
}
