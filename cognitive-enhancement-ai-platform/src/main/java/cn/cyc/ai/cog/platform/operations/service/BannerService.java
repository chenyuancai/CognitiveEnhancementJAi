package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Banner;
import cn.cyc.ai.cog.platform.operations.dto.BannerPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.BannerSaveRequest;
import cn.cyc.ai.cog.platform.operations.repository.BannerRepository;
import org.springframework.stereotype.Service;

/**
 * Banner服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class BannerService {

    /** banner仓储。 */
    private final BannerRepository bannerRepository;

    /**
     * 创建Banner服务。
     *
     * @param bannerRepository banner仓储
     */
    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<Banner> page(BannerPageQuery query) {
        return bannerRepository.page(query);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public Banner detail(Long id) {
        return bannerRepository.findById(id);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public Banner create(BannerSaveRequest request) {
        return bannerRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public Banner update(Long id, BannerSaveRequest request) {
        return bannerRepository.update(id, request);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public void delete(Long id) {
        bannerRepository.delete(id);
    }
}
