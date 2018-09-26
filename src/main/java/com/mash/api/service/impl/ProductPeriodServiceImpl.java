package com.mash.api.service.impl;

import com.mash.api.entity.ProductPeriod;
import com.mash.api.repository.ProductPeriodRepository;
import com.mash.api.service.ProductPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductPeriodServiceImpl implements ProductPeriodService {

    @Autowired
    private ProductPeriodRepository productPeriodRepository;

    @Override
    public List<ProductPeriod> findByProductId(Integer productId) {
        return productPeriodRepository.findByProductId(productId);
    }

    @Override
    public List<ProductPeriod> findAll() {
        return productPeriodRepository.findAll();
    }

    @Override
    public ProductPeriod save(ProductPeriod productPeriod) {
        return productPeriodRepository.save(productPeriod);
    }

    @Override
    public void updateState(Integer id, Integer state) {
        productPeriodRepository.updateState(id, state);
    }

    @Override
    public List<ProductPeriod> findByScheduleId(Integer scheduleId) {
        return productPeriodRepository.findByScheduleId(scheduleId);
    }
}
