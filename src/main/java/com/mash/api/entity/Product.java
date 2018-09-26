package com.mash.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Product{

    @Id
    @GeneratedValue
    private Integer id;
    private Integer pointId;
    private Integer accountId;
    private String name;
    /**
     * 广告位类型
     * 公交站牌
     * 高速公路
     * 。。。
     */
    private String productType;

    /**
     * 商业类
     * 公益类
     */
    private String useType;

    /**
     * 编号
     */
    @NotEmpty(message = "请输入编号")
    private String number;
    private String level;
    /**
     * 价格类型
     * 周期价
     * 竞价
     */
    private Integer priceType;

    /**
     * 交易须知
     */
    private String tradeRule;


    private Date createdTime;
    private Date updateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<BidJoinUser> bidJoinUsers;

    @OneToOne(mappedBy = "product", fetch = FetchType.EAGER)
    private BidParameter bidParameter;

    @JsonBackReference
    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private BidResult bidResult;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<BidRecord> bidRecords;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<ProductPrice> prices;

    @JsonBackReference
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<ProductPeriod> periods;

    @OneToOne(mappedBy = "product", fetch = FetchType.EAGER)
    private ProductAttribute attribute;

    @JsonBackReference
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<OrderProduct> orderProducts;

    @JsonBackReference
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<Shopcart> shopcarts;


    public Product() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getPriceType() {
        return priceType;
    }

    public void setPriceType(Integer priceType) {
        this.priceType = priceType;
    }

    public String getTradeRule() {
        return tradeRule;
    }

    public void setTradeRule(String tradeRule) {
        this.tradeRule = tradeRule;
    }

    public Set<ProductImage> getImages() {
        return images;
    }

    public void setImages(Set<ProductImage> images) {
        this.images = images;
    }

    public BidParameter getBidParameter() {
        return bidParameter;
    }

    public void setBidParameter(BidParameter bidParameter) {
        this.bidParameter = bidParameter;
    }

    public Set<ProductPrice> getPrices() {
        return prices;
    }

    public void setPrices(Set<ProductPrice> prices) {
        this.prices = prices;
    }

    public Set<ProductPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<ProductPeriod> periods) {
        this.periods = periods;
    }

    public ProductAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ProductAttribute attribute) {
        this.attribute = attribute;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Set<Shopcart> getShopcarts() {
        return shopcarts;
    }

    public void setShopcarts(Set<Shopcart> shopcarts) {
        this.shopcarts = shopcarts;
    }

    public Set<BidRecord> getBidRecords() {
        return bidRecords;
    }

    public void setBidRecords(Set<BidRecord> bidRecords) {
        this.bidRecords = bidRecords;
    }

    public BidResult getBidResult() {
        return bidResult;
    }

    public void setBidResult(BidResult bidResult) {
        this.bidResult = bidResult;
    }

    public Set<BidJoinUser> getBidJoinUsers() {
        return bidJoinUsers;
    }

    public void setBidJoinUsers(Set<BidJoinUser> bidJoinUsers) {
        this.bidJoinUsers = bidJoinUsers;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }
}
