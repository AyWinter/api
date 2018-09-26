package com.mash.api.service;

import com.mash.api.entity.Enterprise;

public interface EnterpriseService {

    Enterprise save(Enterprise enterprise);

    Enterprise getById(Integer id);

    Enterprise getByAccountId(Integer accountId);
}
