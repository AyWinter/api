package com.mash.api.service;

import com.mash.api.entity.BidJoinUser;

public interface BidJoinUserService {

    BidJoinUser save(BidJoinUser bidJoinUser);

    BidJoinUser findByAccountIdAndProductId(Integer accountId, Integer productId);

    void deleteById(Integer id);
}
