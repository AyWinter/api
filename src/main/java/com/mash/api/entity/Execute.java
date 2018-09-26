package com.mash.api.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 执行单
 */
@Entity
public class Execute {

    @Id
    @GeneratedValue
    private Integer id;

    private String number;

    /**
     * 0 未分配
     * 1 已分配工人安装
     * 2 安装完成等待审核
     * 3 安装成功
     */
    private Integer state;

    /**
     * 安装工人
     */
    private String worker;

    /**
     * 安装工人ID
     */
    private Integer workerAccountId;

    /**
     * 计划安装时间
     */
    private Date planInstallTime;

    /**
     * 时间安装时间
     */
    private Date actualInstallTime;

    private Integer vendorId;

    /**
     * 安装完成上传图片
     */
    private String img;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public Date getPlanInstallTime() {
        return planInstallTime;
    }

    public void setPlanInstallTime(Date planInstallTime) {
        this.planInstallTime = planInstallTime;
    }

    public Date getActualInstallTime() {
        return actualInstallTime;
    }

    public void setActualInstallTime(Date actualInstallTime) {
        this.actualInstallTime = actualInstallTime;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getWorkerAccountId() {
        return workerAccountId;
    }

    public void setWorkerAccountId(Integer workerAccountId) {
        this.workerAccountId = workerAccountId;
    }
}
