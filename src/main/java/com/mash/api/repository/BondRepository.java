package com.mash.api.repository;

import com.mash.api.entity.Bond;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BondRepository extends JpaRepository<Bond, Integer>{

    Bond findByAccountIdAndProductId(Integer accountId, Integer productId);

    List<Bond> findByAccountId(Integer accountId);
}
