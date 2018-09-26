package com.mash.api.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Refund {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer vendorId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private MainOrder order;

    /**
     * 1 审核中
     * 2 同意退款
     * 3 拒绝退款
     */
    private Integer state;

    /**
     * 备注
     */
    private String explainText;

    /**
     * 审核人
     */
    private String operator;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 申请时间
     */
    private Date applyTime;

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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public MainOrder getOrder() {
        return order;
    }

    public void setOrder(MainOrder order) {
        this.order = order;
    }
}
