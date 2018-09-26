package com.mash.api.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 画面库
 */
@Entity
public class PictureLibrary {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer vendorId;

    private String filePath;

    /**
     * 排期单号
     */
    private String scheduleNumber;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 审核状态
     * 0 审核中
     * 1 审核成功
     * 2 审核失败
     * 3 删除
     */
    private Integer state;

    /**
     * 说明
     */
    private String explainText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getExplainText() {
        return explainText;
    }

    public void setExplainText(String explainText) {
        this.explainText = explainText;
    }
}
