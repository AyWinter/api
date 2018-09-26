package com.mash.api.service;

import com.mash.api.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {

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
     * 保存客户
     * @param customer
     * @return
     */
    Customer save(Customer customer);

    /**
     * 查询客户详细信息
     * @param id
     * @return
     */
    Customer findById(Integer id);

    /**
     * 根据电话号码查询客户
     * @param phone
     * @return
     */
    Customer findByPhone(String phone);

    /**
     * 删除客户信息
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 条件查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Customer> findByParams(Specification<Customer> specification, Pageable pageable);

    Customer findByAccountId(Integer accountId);
}
