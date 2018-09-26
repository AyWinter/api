package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 排期表
 */
@Entity
public class ProductPeriod implements Serializable{

    @Id
    @GeneratedValue
    private Integer id;
    private Date startTime;
    private Date endTime;

    /**
     * 正面画面
     */
    private String positivePicture;
    /**
     * 背面画面
     */
    private String backPicture;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

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

    public ProductPeriod() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getPositivePicture() {
        return positivePicture;
    }

    public void setPositivePicture(String positivePicture) {
        this.positivePicture = positivePicture;
    }

    public String getBackPicture() {
        return backPicture;
    }

    public void setBackPicture(String backPicture) {
        this.backPicture = backPicture;
    }
}
