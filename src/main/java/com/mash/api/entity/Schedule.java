package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * 排期单
 */
@Entity
public class Schedule {

    @Id
    @GeneratedValue
    private Integer id;

    /**
     * 排期单号
     */
    private String number;

    /**
     * 排期状态
     * 0 已做成
     * 1 已提交
     * 2 已审核
     * 3 已锁定
     * 4 已签
     * 5 下单
     * 6 终止
     * 7 删除
     */
    private Integer state;

    /**
     * 创建人员
     */
    private String createdName;

    private Integer vendorId;

    /**
     * 创建人ID
     */
    private Integer createdUserId;

    private Date createdTime;

    @OneToOne(mappedBy = "schedule", fetch = FetchType.LAZY)
    private MainOrder mainOrder;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.EAGER)
    private Set<ProductPeriod> productPeriods;

    @JsonBackReference
    @OneToOne(mappedBy = "schedule", fetch = FetchType.LAZY)
    private Execute execute;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<ProductPeriod> getProductPeriods() {
        return productPeriods;
    }

    public void setProductPeriods(Set<ProductPeriod> productPeriods) {
        this.productPeriods = productPeriods;
    }

    public Execute getExecute() {
        return execute;
    }

    public void setExecute(Execute execute) {
        this.execute = execute;
    }

    public MainOrder getMainOrder() {
        return mainOrder;
    }

    public void setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
    }

    public String getCreatedName() {
        return createdName;
    }

    public void setCreatedName(String createdName) {
        this.createdName = createdName;
    }

    public Integer getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Integer createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
