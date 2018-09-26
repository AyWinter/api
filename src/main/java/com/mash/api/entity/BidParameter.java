package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

@Entity
public class BidParameter {

    @Id
    @GeneratedValue
    private Integer id;
    /**
     * 起拍价格
     */
    private float startPrice;

    /**
     * 价格单位
     */
    private String priceUnit;
    /**
     * 保留价
     */
    private float reservePrice;
    /**
     * 保证金
     */
    private float bidPrice;
    /**
     * 递增金额
     */
    private float increasePrice;

    /**
     * 参与方式
     * 0 无人员限制
     * 1 指定参与人员
     */
    private Integer joinMode;

    /**
     * 参与条件
     */
    private String joinRule;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 广告位交付时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliverTime;

    // 距离结束剩余时间或者距离结束开始时间
    @Transient
    private long diffTime;
    @Transient
    private Integer diffTimeType;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public BidParameter() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float startPrice) {
        this.startPrice = startPrice;
    }

    public float getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(float reservePrice) {
        this.reservePrice = reservePrice;
    }

    public float getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(float bidPrice) {
        this.bidPrice = bidPrice;
    }

    public float getIncreasePrice() {
        return increasePrice;
    }

    public void setIncreasePrice(float increasePrice) {
        this.increasePrice = increasePrice;
    }

    public Integer getJoinMode() {
        return joinMode;
    }

    public void setJoinMode(Integer joinMode) {
        this.joinMode = joinMode;
    }

    public String getJoinRule() {
        return joinRule;
    }

    public void setJoinRule(String joinRule) {
        this.joinRule = joinRule;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Date deliverTime) {
        this.deliverTime = deliverTime;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public long getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(long diffTime) {
        this.diffTime = diffTime;
    }

    public Integer getDiffTimeType() {
        return diffTimeType;
    }

    public void setDiffTimeType(Integer diffTimeType) {
        this.diffTimeType = diffTimeType;
    }
}
