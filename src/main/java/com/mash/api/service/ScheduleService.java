package com.mash.api.service;

import com.mash.api.entity.Schedule;

import java.util.List;

public interface ScheduleService {

    /**
     * 更新排期状态
     * @param id
     * @param state
     */
    void updateState(Integer id, Integer state);

    /**
     * 保存排期
     * @param schedule
     * @return
     */
    Schedule save(Schedule schedule);

    /**
     * 查询所有排期
     * @return
     */
    List<Schedule> findAll();

    Schedule findByNumber(String number);

    Schedule findById(Integer id);
}
