package com.mash.api.entity;

import java.util.List;

public class ParameterResult {

    private List<Parameter> productType;
    private List<Parameter> productLevel;
    /**
     * 材质
     */
    private List<Parameter> material;
    private List<Parameter> lighting;
    /**
     * 人流量
     */
    private List<Parameter> visitorsFlowrate;
    /**
     * 车流量
     */
    private List<Parameter> vehicleFlowrate;

    private List<Parameter> useType;

    public List<Parameter> getProductType() {
        return productType;
    }

    public void setProductType(List<Parameter> productType) {
        this.productType = productType;
    }

    public List<Parameter> getProductLevel() {
        return productLevel;
    }

    public void setProductLevel(List<Parameter> productLevel) {
        this.productLevel = productLevel;
    }

    public List<Parameter> getMaterial() {
        return material;
    }

    public void setMaterial(List<Parameter> material) {
        this.material = material;
    }

    public List<Parameter> getLighting() {
        return lighting;
    }

    public void setLighting(List<Parameter> lighting) {
        this.lighting = lighting;
    }

    public List<Parameter> getVisitorsFlowrate() {
        return visitorsFlowrate;
    }

    public void setVisitorsFlowrate(List<Parameter> visitorsFlowrate) {
        this.visitorsFlowrate = visitorsFlowrate;
    }

    public List<Parameter> getVehicleFlowrate() {
        return vehicleFlowrate;
    }

    public void setVehicleFlowrate(List<Parameter> vehicleFlowrate) {
        this.vehicleFlowrate = vehicleFlowrate;
    }

    public List<Parameter> getUseType() {
        return useType;
    }

    public void setUseType(List<Parameter> useType) {
        this.useType = useType;
    }
}
