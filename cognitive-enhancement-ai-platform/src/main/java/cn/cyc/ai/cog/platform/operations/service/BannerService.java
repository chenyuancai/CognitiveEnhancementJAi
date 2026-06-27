package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Banner;
import cn.cyc.ai.cog.platform.operations.dto.BannerPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.BannerSaveRequest;
import cn.cyc.ai.cog.platform.operations.repository.BannerRepository;
import org.springframework.stereotype.Service;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public PageResult<Banner> page(BannerPageQuery query) {
        return bannerRepository.page(query);
    }

    public Banner detail(Long id) {
        return bannerRepository.findById(id);
    }

    public Banner create(BannerSaveRequest request) {
        return bannerRepository.create(request);
    }

    public Banner update(Long id, BannerSaveRequest request) {
        return bannerRepository.update(id, request);
    }

    public void delete(Long id) {
        bannerRepository.delete(id);
    }
}
