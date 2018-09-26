package com.mash.api.service;

import com.mash.api.entity.PictureLibrary;

import java.util.List;

public interface PictureLibraryService {

    PictureLibrary save(PictureLibrary pictureLibrary);

    List<PictureLibrary> findByScheduleNumber(String scheduleNumber);

    void deleteById(Integer id);

    PictureLibrary findById(Integer id);
}
