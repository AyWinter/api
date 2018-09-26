package com.mash.api.service;

import com.mash.api.entity.Shopcart;

import java.util.List;

public interface ShopcartService {

    List<Shopcart> findByAccountId(Integer accountId);

    List<Shopcart> findByAccountIdAndSelected(Integer accountId, boolean selected);

    Shopcart save(Shopcart shopcart);

    void delete(Integer id);

    void deleteByAccountId(Integer accountId);

    void updateSelected(boolean selected, Integer id);

    void deleteByAccountIdAndProductId(Integer accountId, Integer productId);
}
