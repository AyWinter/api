package com.mash.api.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Person {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer accountId;

    @NotEmpty(message = "请输入姓名")
    private String name;

    @NotEmpty(message = "请输入身份证号码")
    @Size(max = 18, min = 18)
    private String idcardNo;

    // 身份证照片正面
    private String idcardImgFront;
    // 反面
    private String idcardImgBack;

    private Date createdTime;

    private Date updatedTime;

    /**
     * 状态
     * 0 审核中
     * 1 审核成功
     * 2 审核失败
     */
    private Integer state;

    private Date examineTime;

    private String operator;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcardNo() {
        return idcardNo;
    }

    public void setIdcardNo(String idcardNo) {
        this.idcardNo = idcardNo;
    }

    public String getIdcardImgFront() {
        return idcardImgFront;
    }

    public void setIdcardImgFront(String idcardImgFront) {
        this.idcardImgFront = idcardImgFront;
    }

    public String getIdcardImgBack() {
        return idcardImgBack;
    }

    public void setIdcardImgBack(String idcardImgBack) {
        this.idcardImgBack = idcardImgBack;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getExamineTime() {
        return examineTime;
    }

    public void setExamineTime(Date examineTime) {
        this.examineTime = examineTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
