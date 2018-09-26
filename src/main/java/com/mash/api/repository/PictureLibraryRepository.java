package com.mash.api.repository;

import com.mash.api.entity.PictureLibrary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PictureLibraryRepository extends JpaRepository<PictureLibrary, Integer>{

    Page<PictureLibrary> findAll(Specification<PictureLibrary> specification, Pageable pageable);

    List<PictureLibrary> findByScheduleNumber(String scheduleNumber);

    /**
     * 更新画面状态
     * @param id
     * @param state
     * @param explain
     */
    @Transactional
    @Query(value="update picture_library set state = ?2, explain_text = ?3 where id = ?1", nativeQuery = true)
    @Modifying
    void updateState(Integer id, Integer state, String explain);
}
