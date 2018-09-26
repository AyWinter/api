package com.mash.api.repository;

import com.mash.api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>{

    // List<Product> findByProductTypeAndAreaAndPriceType(String productType, String area, Integer priceType);

    List<Product> findByProductTypeAndPriceType(String productType, Integer priceType);

    List<Product> findByAccountId(Integer accountId);

    List<Product> findByPriceType(Integer priceType);

    List<Product> findByAccountIdAndPriceType(Integer accountId, Integer priceType);

    Page<Product> findByAccountIdAndPriceType(Pageable pageable, Integer accountId, Integer priceType);

    Page<Product> findByAccountId(Pageable pageable, Integer accountId);

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    /**
     * 前台地图搜索用
     * @param specification
     * @return
     */
    List<Product> findAll(Specification<Product> specification);

    @Query(value="select id from product where price_type=0", nativeQuery = true)
    List<Integer> findProductId();

    /**
     * 热门广告位
     * @param vendorId
     * @return
     */
    @Query(value="select * from product where price_type=0 and account_id=?1 limit 0, 3", nativeQuery = true)
    List<Product> hotProduct(Integer vendorId);

    /**
     * 查询站点下所有广告位
     * @param positionId
     * @return
     */
    List<Product> findByPositionId(Integer positionId);
}
