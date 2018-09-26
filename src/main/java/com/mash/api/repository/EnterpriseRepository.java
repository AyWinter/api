package com.mash.api.repository;

import com.mash.api.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Integer> {

    Enterprise findByAccountId(Integer accountId);
}
