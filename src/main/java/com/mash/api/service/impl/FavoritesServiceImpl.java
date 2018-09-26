package com.mash.api.service.impl;

import com.mash.api.entity.Favorites;
import com.mash.api.repository.FavoritesRepository;
import com.mash.api.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Override
    public Favorites save(Favorites favorites) {
        return favoritesRepository.save(favorites);
    }

    @Override
    public void remove(Favorites favorites) {
        favoritesRepository.delete(favorites);
    }

    @Override
    public List<Favorites> findByAccountId(Integer accountId) {
        return favoritesRepository.findByAccountId(accountId);
    }

    @Override
    public Favorites findByAccountIdAndPositionId(Integer accountId, Integer positionId) {
        return favoritesRepository.findByAccountIdAndPositionId(accountId, positionId);
    }

    @Override
    public void removeAll(Integer accountId) {
        favoritesRepository.removeAll(accountId);
    }
}
