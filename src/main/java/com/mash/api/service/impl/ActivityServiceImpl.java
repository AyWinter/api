package com.mash.api.service.impl;

import com.mash.api.entity.Activity;
import com.mash.api.repository.ActivityRepository;
import com.mash.api.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> findByAccountId(Integer accountId) {
        return activityRepository.findByAccountId(accountId);
    }

    @Override
    public void deleteById(Integer id) {
        activityRepository.delete(id);
    }

    @Override
    public List<Activity> findByAccountId2(Integer accountId) {
        return activityRepository.findByAccountId2(accountId);
    }

    @Override
    public Activity findById(Integer id) {
        return activityRepository.findOne(id);
    }

    @Override
    public Page<Activity> findByAccountId(Pageable pageable, Integer accountId) {
        return activityRepository.findByAccountId(pageable, accountId);
    }
}
