package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class Bond {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer accountId;
    private Integer productId;

    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private MainOrder order;

    public Bond() {
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public MainOrder getOrder() {
        return order;
    }

    public void setOrder(MainOrder order) {
        this.order = order;
    }
}
