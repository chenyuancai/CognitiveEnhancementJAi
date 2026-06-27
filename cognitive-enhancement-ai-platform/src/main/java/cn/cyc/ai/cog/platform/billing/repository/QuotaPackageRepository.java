package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.dto.PackagePageQuery;
import cn.cyc.ai.cog.platform.billing.dto.QuotaPackageSaveRequest;

import java.util.List;

/**
 * 额度包仓储接口。
 */
public interface QuotaPackageRepository {

    PageResult<QuotaPackage> page(PackagePageQuery query);

    List<QuotaPackage> listOnSale(String segment);

    QuotaPackage requireById(Long id);

    QuotaPackage save(Long id, QuotaPackageSaveRequest request);
}
