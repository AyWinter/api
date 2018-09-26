package com.mash.api.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 操作记录
 */
@Entity
public class OperateRecord {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer vendorId;
    /**
     * 类型
     * 0 审核排期单
     * 1 生成执行单
     * 2 画面审核
     * 3 分配执行单给安装工人
     * 4 作成排期单
     */
    private Integer type;
    private Date operateTime;
    private String operateName;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getExplainText() {
        return explainText;
    }

    public void setExplainText(String explainText) {
        this.explainText = explainText;
    }
}
