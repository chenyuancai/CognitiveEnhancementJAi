package cn.cyc.ai.cog.common.mybatis;

import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充：创建/更新时间、操作人、租户、逻辑删除位。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class AuditMetaObjectHandler implements MetaObjectHandler {

    /**
     * 执行insertFill。
     *
     * @param metaObject metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "createBy", Long.class, UserContext.currentUserId());
        strictInsertFill(metaObject, "updateBy", Long.class, UserContext.currentUserId());
        if (getFieldValByName("tenantId", metaObject) == null) {
            strictInsertFill(metaObject, "tenantId", Long.class, TenantContext.currentTenantId());
        }
        if (getFieldValByName("deleted", metaObject) == null) {
            strictInsertFill(metaObject, "deleted", Integer.class, 0);
        }
        if (getFieldValByName("version", metaObject) == null) {
            strictInsertFill(metaObject, "version", Integer.class, 0);
        }
    }

    /**
     * 更新Fill。
     *
     * @param metaObject metaObject
     * @return 更新结果
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        strictUpdateFill(metaObject, "updateBy", Long.class, UserContext.currentUserId());
    }
}
