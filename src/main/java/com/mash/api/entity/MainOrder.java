package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class MainOrder{

    @Id
    @GeneratedValue
    private Integer id;
    private Integer accountId;
    private String orderNo;
    private Integer vendorId;
    /**
     * 订单类型
     * 1 购买广告位订单
     * 2 支付保证金订单
     */
    private Integer type;
    /**
     * 订单金额
     */
    private float amount;
    /**
     * 折扣金额
     */
    private float discountAmount;
    private Date createdTime;
    /**
     * 订单状态
     * 0 未支付
     * 1 支付成功
     * 2 付款失败
     * 3 已关闭
     */
    private Integer state;

    /**
     * 付款方式
     * 0  支付宝
     * 1  微信
     */
    private Integer payMethod;

    /**
     * 付款时间
     */
    private Date payTime;

    /**
     * 订单留言
     */
    private String message;

    /**
     * 退款状态
     * 0 未退款
     * 1 退款成功
     * 2 审核中
     * 3 拒绝退款
     */
    private Integer refundState;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款申请时间
     */
    private Date applyTime;

    /**
     * 平台反馈时间
     */
    private Date feedbackTime;

    /**
     * 平台反馈
     */
    private String feedback;

    /**
     * 退款金额
     */
    private float refundAmount;

    /**
     * 微信支付订单号（申请退款时候用）
     */
    private String transactionId;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 支付剩余时间（默认下单后30分钟内支付，否者订单自动取消）
     */
    @Transient
    private long surplusTime;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<OrderProduct> orderProducts;

    @OneToOne(mappedBy = "order", fetch = FetchType.EAGER)
    private Receiver receiver;

    @OneToOne(mappedBy = "order", fetch = FetchType.EAGER)
    private Bond bond;

    @JsonBackReference
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Refund refund;

    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public MainOrder() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public Bond getBond() {
        return bond;
    }

    public void setBond(Bond bond) {
        this.bond = bond;
    }

    public Integer getRefundState() {
        return refundState;
    }

    public void setRefundState(Integer refundState) {
        this.refundState = refundState;
    }

    public float getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(float refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public long getSurplusTime() {
        return surplusTime;
    }

    public void setSurplusTime(long surplusTime) {
        this.surplusTime = surplusTime;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(Date feedbackTime) {
        this.feedbackTime = feedbackTime;
    }
}
