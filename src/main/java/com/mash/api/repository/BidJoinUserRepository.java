package com.mash.api.repository;

import com.mash.api.entity.BidJoinUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidJoinUserRepository extends JpaRepository<BidJoinUser, Integer>{

    BidJoinUser findByAccountIdAndProductId(Integer accountId, Integer productId);
}
