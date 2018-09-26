package com.mash.api.service.impl;

import com.mash.api.entity.Demand;
import com.mash.api.repository.DemandRepository;
import com.mash.api.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandServiceImpl implements DemandService {

    @Autowired
    private DemandRepository demandRepository;

    @Override
    public Demand save(Demand demand) {
        return demandRepository.save(demand);
    }

    @Override
    public Demand findById(Integer id) {
        return demandRepository.findOne(id);
    }

    @Override
    public List<Demand> findAll() {
        return demandRepository.findAllOrderByPublishTimeDesc();
    }

    @Override
    public List<Demand> findByAccountId(Integer accountId) {
        return demandRepository.findByAccountId(accountId);
    }
}
