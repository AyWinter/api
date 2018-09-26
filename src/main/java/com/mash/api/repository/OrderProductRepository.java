package com.mash.api.repository;

import com.mash.api.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer>{

    List<OrderProduct> findByOrderId(Integer orderId);

    OrderProduct findByProductId(Integer productId);

    @Query(value="select * from order_product where product_id in (select id from product where account_id=?1 ) order by order_id desc", nativeQuery = true)
    List<OrderProduct> findOrderProductByVendorId(Integer vendorId);
}
