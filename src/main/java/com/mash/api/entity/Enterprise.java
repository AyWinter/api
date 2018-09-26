package com.mash.api.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Enterprise {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer accountId;
    @NotEmpty(message = "请输入公司名称")
    private String name;
    @NotEmpty(message = "请输入公司地址")
    private String address;
    private String businessLicense;
    @NotEmpty(message = "请输入公司电话")
    private String phoneNo;
    @NotEmpty(message = "请输入法人姓名")
    private String legalPerson;
    @NotEmpty(message = "请输入法人身份证号码")
    private String legalPersonIdcardNo;
    private String legalPersonIdcard;
    @NotEmpty(message = "请输入开户银行")
    private String depositBank;
    @NotEmpty(message = "请输入开户银行支行")
    private String depositBankBranch;
    @NotEmpty(message = "请输入开户银行账号")
    private String accountNumber;
    private Date createdTime;
    private Date updatedTime;
    /**
     * 0 审核中
     * 1 审核成功
     * 2 审核失败
     */
    private Integer state;
    private Date examineTime;
    private String operator;

    @OneToMany(mappedBy = "enterprise", fetch = FetchType.EAGER)
    private Set<Department> departmentSet;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getLegalPersonIdcardNo() {
        return legalPersonIdcardNo;
    }

    public void setLegalPersonIdcardNo(String legalPersonIdcardNo) {
        this.legalPersonIdcardNo = legalPersonIdcardNo;
    }

    public String getLegalPersonIdcard() {
        return legalPersonIdcard;
    }

    public void setLegalPersonIdcard(String legalPersonIdcard) {
        this.legalPersonIdcard = legalPersonIdcard;
    }

    public String getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(String depositBank) {
        this.depositBank = depositBank;
    }

    public String getDepositBankBranch() {
        return depositBankBranch;
    }

    public void setDepositBankBranch(String depositBankBranch) {
        this.depositBankBranch = depositBankBranch;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    public Set<Department> getDepartmentSet() {
        return departmentSet;
    }

    public void setDepartmentSet(Set<Department> departmentSet) {
        this.departmentSet = departmentSet;
    }
}
