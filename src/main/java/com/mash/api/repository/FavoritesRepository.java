package com.mash.api.repository;

import com.mash.api.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {

    List<Favorites> findByAccountId(Integer accountId);

    Favorites findByAccountIdAndPositionId(Integer accountId, Integer positionId);

    @Transactional
    @Query(value="delete from favorites where account_id=?1", nativeQuery = true)
    @Modifying
    void removeAll(Integer accountId);
}
