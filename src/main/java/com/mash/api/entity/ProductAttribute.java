package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class ProductAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    private float length;

    private float width;

    private String material;

    private String lighting;

    private String visitorsFlowrate;

    private String vehicleFlowrate;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductAttribute() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getLighting() {
        return lighting;
    }

    public void setLighting(String lighting) {
        this.lighting = lighting;
    }

    public String getVisitorsFlowrate() {
        return visitorsFlowrate;
    }

    public void setVisitorsFlowrate(String visitorsFlowrate) {
        this.visitorsFlowrate = visitorsFlowrate;
    }

    public String getVehicleFlowrate() {
        return vehicleFlowrate;
    }

    public void setVehicleFlowrate(String vehicleFlowrate) {
        this.vehicleFlowrate = vehicleFlowrate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
