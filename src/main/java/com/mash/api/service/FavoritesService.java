package com.mash.api.service;

import com.mash.api.entity.Favorites;

import java.util.List;

public interface FavoritesService {

    Favorites save(Favorites favorites);

    void remove(Favorites favorites);

    void removeAll(Integer accountId);

    List<Favorites> findByAccountId(Integer accountId);

    Favorites findByAccountIdAndPositionId(Integer accountId, Integer positionId);
}
