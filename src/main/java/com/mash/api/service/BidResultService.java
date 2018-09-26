package com.mash.api.service;

import com.mash.api.entity.BidResult;

import java.util.List;

public interface BidResultService {

    BidResult save(BidResult bidResult);

    void updateState(Integer id, Integer state);

    void updateState2(Integer productId, Integer state);

    BidResult findByProductId(Integer productId);

    BidResult findByProductIdAndAccountId(Integer productId, Integer accountId);

    List<BidResult> findByAccountIdAndState(Integer accountId, Integer state);

    List<BidResult> findAll();
}
