package com.mash.api.service.impl;

import com.mash.api.entity.Bond;
import com.mash.api.repository.BondRepository;
import com.mash.api.service.BondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BondServiceImpl implements BondService{

    @Autowired
    private BondRepository bondRepository;

    @Override
    public Bond findByAccountIdAndProductId(Integer accountId, Integer productId) {
        return bondRepository.findByAccountIdAndProductId(accountId, productId);
    }

    @Override
    public List<Bond> findByAccountId(Integer accountId) {
        return bondRepository.findByAccountId(accountId);
    }
}
