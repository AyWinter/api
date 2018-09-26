package com.mash.api.repository;

import com.mash.api.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

    /**
     * 查询企业内所有客户
     * @param vendorId
     * @return
     */
    Page<Customer> findByVendorId(Pageable pageable, Integer vendorId);

    /**
     * 查询企业内某个客户经理下的所有客户
     * @param vendorId
     * @param employeeId
     * @return
     */
    Page<Customer> findByVendorIdAndEmployeeId(Pageable pageable, Integer vendorId, Integer employeeId);

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Customer> findAll(Specification<Customer> specification, Pageable pageable);

    /**
     * 根据电话号码查询客户
     * @param phone
     * @return
     */
    Customer findByPhone(String phone);

    Customer findByAccountId(Integer accountId);
}
