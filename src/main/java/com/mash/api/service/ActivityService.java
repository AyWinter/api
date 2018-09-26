package com.mash.api.service;

import com.mash.api.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

    Activity save(Activity activity);

    List<Activity> findByAccountId(Integer accountId);

    List<Activity> findByAccountId2(Integer accountId);

    Page<Activity> findByAccountId(Pageable pageable, Integer accountId);

    void deleteById(Integer id);

    Activity findById(Integer id);
}
