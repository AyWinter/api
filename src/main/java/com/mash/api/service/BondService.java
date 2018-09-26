package com.mash.api.service;

import com.mash.api.entity.Bond;

import java.util.List;

public interface BondService {

    Bond findByAccountIdAndProductId(Integer accountId, Integer productId);

    List<Bond> findByAccountId(Integer accountId);
}
