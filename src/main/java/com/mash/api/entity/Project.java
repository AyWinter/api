package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Project {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer vendorId;

    /**
     * 项目名称
     */
    @NotEmpty(message = "请输入项目名称")
    private String name;

    /**
     * 项目编号
     */
    @NotEmpty(message = "请输入项目编号")
    private String number;

    /**
     * 立项时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createdTime;

    /**
     * 项目类型
     * 0 媒体发布
     */
    private Integer type;

    /**
     * 业务部门
     */
    @NotEmpty(message = "请选择业务部门")
    private String department;

    /**
     * 业务人员
     */
    @NotEmpty(message = "请选择业务人员")
    private String employee;

    /**
     * 项目状态
     * 0 活动
     * 1 关闭
     */
    private Integer state;

    /**
     * 是否生成排期单
     * 0 未生成排期单
     * 1 已生成排期单
     */
    private Integer scheduleState;

    /**
     * 流转状态
     * 0 流转钟
     * 1 已完毕
     * 2 已驳回
     */
    private Integer examineState;

    /**
     * 审核说明
     */
    private String examineExplain;

    /**
     * 成单状态
     * 0 跟进
     * 1 赢单
     * 2 去单
     */
    private Integer tradeState;

    /**
     * 状态日期
     */
    private Date stateTime;

    /**
     * 项目人数
     */
    private Integer peopleCount;

    /**
     * 执行开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date executeStartTime;

    /**
     * 执行结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date executeEndTime;

    /**
     * 项目描述
     */
    private String describeText;

    /**
     * 立项原因
     */
    private String reason;

    /**
     * 关闭原因
     */
    private String cancelResson;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 排期表
     */
    @JsonBackReference
    @OneToOne(mappedBy = "project", fetch = FetchType.LAZY)
    private Schedule schedule;

    /**
     * 客户表
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * 操作时间
     */
    private Date operateTime;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Date getStateTime() {
        return stateTime;
    }

    public void setStateTime(Date stateTime) {
        this.stateTime = stateTime;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public Date getExecuteStartTime() {
        return executeStartTime;
    }

    public void setExecuteStartTime(Date executeStartTime) {
        this.executeStartTime = executeStartTime;
    }

    public Date getExecuteEndTime() {
        return executeEndTime;
    }

    public void setExecuteEndTime(Date executeEndTime) {
        this.executeEndTime = executeEndTime;
    }

    public String getDescribeText() {
        return describeText;
    }

    public void setDescribeText(String describeText) {
        this.describeText = describeText;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCancelResson() {
        return cancelResson;
    }

    public void setCancelResson(String cancelResson) {
        this.cancelResson = cancelResson;
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

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getTradeState() {
        return tradeState;
    }

    public void setTradeState(Integer tradeState) {
        this.tradeState = tradeState;
    }

    public Integer getState() {
        return state;
    }

    public Integer getExamineState() {
        return examineState;
    }

    public void setExamineState(Integer examineState) {
        this.examineState = examineState;
    }

    public String getExamineExplain() {
        return examineExplain;
    }

    public void setExamineExplain(String examineExplain) {
        this.examineExplain = examineExplain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Integer getScheduleState() {
        return scheduleState;
    }

    public void setScheduleState(Integer scheduleState) {
        this.scheduleState = scheduleState;
    }
}
