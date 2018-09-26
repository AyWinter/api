package com.mash.api.service.impl;

import com.mash.api.entity.Shopcart;
import com.mash.api.repository.ShopcartRepository;
import com.mash.api.service.ShopcartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopcartServiceImpl implements ShopcartService{

    @Autowired
    private ShopcartRepository shopcartRepository;

    @Override
    public List<Shopcart> findByAccountId(Integer accountId) {
        return shopcartRepository.findByAccountId(accountId);
    }

    @Override
    public List<Shopcart> findByAccountIdAndSelected(Integer accountId, boolean selected) {
        return shopcartRepository.findByAccountIdAndSelected(accountId, selected);
    }

    @Override
    public Shopcart save(Shopcart shopcart) {
        return shopcartRepository.save(shopcart);
    }

    @Override
    public void delete(Integer id) {
        shopcartRepository.delete(id);
    }

    @Override
    public void deleteByAccountId(Integer accountId) {
        shopcartRepository.deleteByAccountId(accountId);
    }

    @Override
    public void updateSelected(boolean selected, Integer id ) {
        shopcartRepository.updateSelected(selected, id);
    }

    @Override
    public void deleteByAccountIdAndProductId(Integer accountId, Integer productId) {
        shopcartRepository.deleteByAccountIdAndProductId(accountId, productId);
    }
}
