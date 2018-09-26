package com.mash.api.service.impl;

import com.mash.api.entity.BidResult;
import com.mash.api.repository.BidResultRepository;
import com.mash.api.service.BidResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BidResultServiceImpl implements BidResultService {

    @Autowired
    private BidResultRepository bidResultRepository;

    @Override
    public BidResult save(BidResult bidResult) {
        return bidResultRepository.save(bidResult);
    }

    @Override
    public void updateState(Integer id, Integer state) {
        bidResultRepository.updateState(id, state);
    }

    @Override
    public BidResult findByProductId(Integer productId) {
        return bidResultRepository.findByProductId(productId);
    }

    @Override
    public List<BidResult> findByAccountIdAndState(Integer accountId, Integer state) {
        return bidResultRepository.findByAccountIdAndState(accountId, state);
    }

    @Override
    public void updateState2(Integer productId, Integer state) {
        bidResultRepository.updateState2(productId, state);
    }

    @Override
    public BidResult findByProductIdAndAccountId(Integer productId, Integer accountId) {
        return bidResultRepository.findByProductIdAndAccountId(productId, accountId);
    }

    @Override
    public List<BidResult> findAll() {
        return bidResultRepository.findAll();
    }
}
