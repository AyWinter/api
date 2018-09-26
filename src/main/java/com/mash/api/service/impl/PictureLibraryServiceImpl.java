package com.mash.api.service.impl;

import com.mash.api.entity.PictureLibrary;
import com.mash.api.repository.PictureLibraryRepository;
import com.mash.api.service.PictureLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureLibraryServiceImpl implements PictureLibraryService {

    @Autowired
    private PictureLibraryRepository pictureLibraryRepository;

    @Override
    public PictureLibrary save(PictureLibrary pictureLibrary) {
        return pictureLibraryRepository.save(pictureLibrary);
    }

    @Override
    public List<PictureLibrary> findByScheduleNumber(String scheduleNumber) {
        return pictureLibraryRepository.findByScheduleNumber(scheduleNumber);
    }

    @Override
    public void deleteById(Integer id) {
        pictureLibraryRepository.delete(id);
    }

    @Override
    public PictureLibrary findById(Integer id) {
        return pictureLibraryRepository.findOne(id);
    }
}
