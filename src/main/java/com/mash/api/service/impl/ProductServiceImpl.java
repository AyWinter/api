package com.mash.api.service.impl;

import com.mash.api.StaticMetaModel.Product_;
import com.mash.api.entity.*;
import com.mash.api.repository.*;
import com.mash.api.service.*;
import com.mash.api.util.Tools;
import org.hibernate.annotations.NaturalId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private BidParameterRepository bidParameterRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private PositionService positionService;

    @Transactional
    @Override
    public Product save(Product product,
                        BidParameter bidParameter,
                        ProductAttribute productAttribute,
                        String cycles,
                        String cyclesPrice,
                        String img1Path,
                        String img2Path,
                        Integer vendorId,
                        Integer positionId) {

        // 保存广告位基本信息
        product.setAccountId(vendorId);
        product.setCreatedTime(new Date());
        product.setUpdateTime(new Date());
//        String[] locationArray = location.split(",");
//        product.setProvince(locationArray[0]);
//        product.setCity(locationArray[1]);
//        product.setArea(locationArray[2]);
//        product.setAddress(locationArray[3]+locationArray[4]);
        Position position = positionService.findById(positionId);
        product.setName(position.getRoad() + "-" + position.getStation() + "-" + product.getNumber());
        product.setPosition(position);
        product = productRepository.save(product);
        log.info("保存product基本信息ok");

        // 保存图片信息
        if (!Tools.isEmpty(img1Path))
        {
            ProductImage productImage = new ProductImage();
            productImage.setPath(img1Path);
            productImage.setProduct(product);
            productImageRepository.save(productImage);
        }

        if (!Tools.isEmpty(img2Path))
        {
            ProductImage productImage = new ProductImage();
            productImage.setPath(img2Path);
            productImage.setProduct(product);
            productImageRepository.save(productImage);
        }
        log.info("保存image基本信息ok");

        // 保存属性
        productAttribute.setProduct(product);
        productAttributeRepository.save(productAttribute);

        // 保存价格
        if (product.getPriceType() == 0)
        {
            log.info("保存周期价格start");
            // 保存周期价格
            String[] cycleArray = cycles.split(",");
            String[] cyclePriceArray = cyclesPrice.split(",");
            log.info("cycleArray = {}", cycleArray);
            log.info("cyclePriceArray = {}", cyclePriceArray);
            for (int i = 0; i < cycleArray.length; i ++)
            {
                ProductPrice productPrice = new ProductPrice();
                productPrice.setProduct(product);
                productPrice.setCycle(cycleArray[i]);
                productPrice.setPrice(Float.valueOf(cyclePriceArray[i]));

                productPriceRepository.save(productPrice);
            }
            log.info("保存周期价格end");
        }
        else
        {
            // 保存竞价规则
            bidParameter.setProduct(product);
            bidParameterRepository.save(bidParameter);
            log.info("保存竞价规则end");
        }
        return product;
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findOne(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findByProductType(String productType) {
        return productRepository.findByProductTypeAndPriceType(productType,0);
    }

//    @Override
//    public List<Product> findByProductTypeAndArea(String productType, String area) {
//
//        return productRepository.findByProductTypeAndAreaAndPriceType(productType, area, 0);
//    }

    @Override
    public List<Product> findByAccountId(Integer accountId) {
        return productRepository.findByAccountId(accountId);
    }

    @Override
    public List<Product> findByPriceType(Integer priceType) {
        return productRepository.findByPriceType(priceType);
    }

    @Override
    public List<Product> findByAccountIdAndPriceType(Integer accountId, Integer priceType) {
        return productRepository.findByAccountIdAndPriceType(accountId, priceType);
    }

    @Override
    public Page<Product> findByAccountId(Pageable pageable, Integer accountId) {
        return productRepository.findByAccountId(pageable, accountId);
    }

    @Override
    public Page<Product> findByAccountIdAndPriceType(Pageable pageable, Integer accountId, Integer priceType) {
        return productRepository.findByAccountIdAndPriceType(pageable, accountId, priceType);
    }

    @Override
    public Page<Product> findAll(Specification<Product> specification, Pageable pageable) {

        return productRepository.findAll(specification, pageable);
    }

    @Override
    public List<Product> findAll(Specification<Product> specification) {
        return productRepository.findAll(specification);
    }

    @Override
    public List<Integer> findProductId() {
        return productRepository.findProductId();
    }

    @Override
    public List<Product> hotProduct(Integer vendorId) {
        return productRepository.hotProduct(vendorId);
    }

    @Override
    public List<Product> findByPositionId(Integer positionId) {
        return productRepository.findByPositionId(positionId);
    }
}
