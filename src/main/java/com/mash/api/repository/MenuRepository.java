package com.mash.api.repository;

import com.mash.api.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Integer>{


    List<Menu> findByParentIdOrderByOrderIndex(Integer parentId);
}
