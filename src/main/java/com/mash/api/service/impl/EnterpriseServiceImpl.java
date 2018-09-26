package com.mash.api.service.impl;

import com.mash.api.entity.Enterprise;
import com.mash.api.repository.EnterpriseRepository;
import com.mash.api.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnterpriseServiceImpl implements EnterpriseService{

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Override
    public Enterprise save(Enterprise enterprise) {
        return enterpriseRepository.save(enterprise);
    }

    @Override
    public Enterprise getById(Integer id) {
        return enterpriseRepository.findOne(id);
    }

    @Override
    public Enterprise getByAccountId(Integer accountId) {
        return enterpriseRepository.findByAccountId(accountId);
    }
}
