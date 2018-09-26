package com.mash.api.StaticMetaModel;

import com.mash.api.entity.Product;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Product.class)
public class Product_ {

    public static volatile SingularAttribute<Product, Integer> id;
    public static volatile SingularAttribute<Product, Integer> accountId;
    public static volatile SingularAttribute<Product, String> name;
    public static volatile SingularAttribute<Product, String> productType;
    public static volatile SingularAttribute<Product, String> number;
    public static volatile SingularAttribute<Product, String> level;
    public static volatile SingularAttribute<Product, Integer> priceType;
    public static volatile SingularAttribute<Product, String> tradeRule;
    public static volatile SingularAttribute<Product, String> province;
    public static volatile SingularAttribute<Product, String> city;
    public static volatile SingularAttribute<Product, String> area;
    public static volatile SingularAttribute<Product, String> lon;
    public static volatile SingularAttribute<Product, String> lat;

}
