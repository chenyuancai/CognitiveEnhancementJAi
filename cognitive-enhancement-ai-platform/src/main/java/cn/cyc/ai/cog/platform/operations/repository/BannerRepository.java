package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Banner;
import cn.cyc.ai.cog.platform.operations.dto.BannerPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.BannerSaveRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerRepository {

    PageResult<Banner> page(BannerPageQuery query);

    Banner findById(Long id);

    List<Banner> listActiveByPosition(String position, LocalDateTime now);

    Banner create(BannerSaveRequest request);

    Banner update(Long id, BannerSaveRequest request);

    void delete(Long id);
}
