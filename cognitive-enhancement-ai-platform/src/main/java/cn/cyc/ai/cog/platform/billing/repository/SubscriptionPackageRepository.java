package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.dto.PackagePageQuery;
import cn.cyc.ai.cog.platform.billing.dto.SubscriptionPackageSaveRequest;

import java.util.List;

/**
 * 订阅套餐仓储接口。
 */
public interface SubscriptionPackageRepository {

    PageResult<SubscriptionPackage> page(PackagePageQuery query);

    List<SubscriptionPackage> listOnSale(String segment);

    SubscriptionPackage findByPackageCode(String packageCode);

    SubscriptionPackage requireById(Long id);

    SubscriptionPackage save(Long id, SubscriptionPackageSaveRequest request);
}
