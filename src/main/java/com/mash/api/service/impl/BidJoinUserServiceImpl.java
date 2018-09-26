package com.mash.api.service.impl;

import com.mash.api.entity.BidJoinUser;
import com.mash.api.repository.BidJoinUserRepository;
import com.mash.api.service.BidJoinUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BidJoinUserServiceImpl implements BidJoinUserService {

    @Autowired
    private BidJoinUserRepository bidJoinUserRepository;

    @Override
    public BidJoinUser save(BidJoinUser bidJoinUser) {
        return bidJoinUserRepository.save(bidJoinUser);
    }

    @Override
    public BidJoinUser findByAccountIdAndProductId(Integer accountId, Integer productId) {
        return bidJoinUserRepository.findByAccountIdAndProductId(accountId, productId);
    }

    @Override
    public void deleteById(Integer id) {
        bidJoinUserRepository.delete(id);
    }
}
