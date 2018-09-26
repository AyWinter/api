package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class Receiver {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private String mobileNo;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private MainOrder order;

    public Receiver() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public MainOrder getOrder() {
        return order;
    }

    public void setOrder(MainOrder order) {
        this.order = order;
    }
}
