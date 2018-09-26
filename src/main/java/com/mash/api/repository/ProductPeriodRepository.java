package com.mash.api.repository;

import com.mash.api.entity.ProductPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductPeriodRepository extends JpaRepository<ProductPeriod, Integer>{


    @Query(value="select * from product_period where product_id = ?1 and state not in (0, 6, 7)", nativeQuery = true)
    List<ProductPeriod> findByProductId(Integer productId);

    List<ProductPeriod> findAll();

    /**
     * 更新排期状态
     * @param id
     * @param state
     */
    @Transactional
    @Query(value="update product_period set state=?2 where id=?1", nativeQuery = true)
    @Modifying
    void updateState(Integer id, Integer state);

    /**
     * 根据排期ID查询排期
     * @param scheduleId
     * @return
     */
    List<ProductPeriod> findByScheduleId(Integer scheduleId);

    /**
     * 查询排期单所有产品信息
     * @param specification
     * @param pageable
     * @return
     */
    Page<ProductPeriod> findAll(Specification<ProductPeriod> specification, Pageable pageable);

    /**
     * 设置正面图片
     * @param filePath
     * @param idList
     */
    @Transactional
    @Query(value="update product_period set positive_picture=?1 where id in ?2", nativeQuery = true)
    @Modifying
    void setPositivePicture(String filePath, List<Integer> idList);

    /**
     * 设置背面图片
     * @param filePath
     * @param idList
     */
    @Transactional
    @Query(value="update product_period set back_picture=?1 where id in ?2", nativeQuery = true)
    @Modifying
    void setBackPicture(String filePath, List<Integer> idList);
}
