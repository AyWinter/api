package com.mash.api.service.impl;

import com.mash.api.entity.Schedule;
import com.mash.api.repository.ScheduleRepository;
import com.mash.api.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void updateState(Integer id, Integer state) {

        scheduleRepository.updateState(id, state);
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule findByNumber(String number) {
        return scheduleRepository.findByNumber(number);
    }

    @Override
    public Schedule findById(Integer id) {
        return scheduleRepository.findOne(id);
    }
}
