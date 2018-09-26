package com.mash.api.service;

import com.mash.api.entity.Product;
import com.mash.api.entity.ProductPeriod;

import java.util.List;

/**
 * 排期管理service
 */
public interface ProductPeriodService {

    List<ProductPeriod> findByProductId(Integer productId);

    List<ProductPeriod> findAll();

    ProductPeriod save(ProductPeriod productPeriod);

    /**
     * 更新排期状态
     * @param id
     * @param state
     */
    void updateState(Integer id, Integer state);

    /**
     * 根据项目ID查询排期
     * @param scheduleId
     * @return
     */
    List<ProductPeriod> findByScheduleId(Integer scheduleId);
}
