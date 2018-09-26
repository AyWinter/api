package com.mash.api.service;

import com.mash.api.entity.Demand;

import java.util.List;

public interface DemandService {

    Demand save(Demand demand);

    Demand findById(Integer id);

    List<Demand> findAll();

    List<Demand> findByAccountId(Integer accountId);
}
