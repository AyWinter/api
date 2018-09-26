package com.mash.api.controller;

import com.mash.api.annotation.Auth;
import com.mash.api.entity.*;
import com.mash.api.repository.ProductRepository;
import com.mash.api.service.*;
import com.mash.api.service.impl.ProductServiceImpl;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import com.mash.api.util.WebUtil;
import jxl.Workbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.misc.BASE64Encoder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Path;
import javax.validation.Valid;
import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductPeriodService productPeriodService;

    @Autowired
    private PositionService positionService;

    /**
     * 广告位保存
     * @param product
     * @param bidParameter
     * @param bindingResult
     * @param img1Base64
     * @param img2Base64
     * @param cycles
     * @param cyclePrice
     * @param request
     * @return
     */
    @PostMapping(value="/product")
    public Result<Product> save(@Valid Product product,
                                ProductAttribute productAttribute,
                                BindingResult bindingResult,
                                BidParameter bidParameter,
                                @RequestParam("img1Base64") String img1Base64,
                                @RequestParam("img2Base64") String img2Base64,
                                @RequestParam("cycles") String cycles,
                                @RequestParam("cyclePrice") String cyclePrice,
                                @RequestParam("location") String location,
                                @RequestParam("startTimeParam") String startTimeParam,
                                @RequestParam("endTimeParam") String endTimeParam,
                                @RequestParam("deliverTimeParam") String deliverTimeParam,
                                HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        // 获取vendorId
        Integer vendorId = findVendorId(accountId);
        if (vendorId == 0)
        {
            return ResultUtil.fail(-1, "暂时无权限发布广告位，请通过资源方审核后在进行发布");
        }

        if (bindingResult.hasErrors())
        {
            return ResultUtil.fail(-1, bindingResult.getFieldError().getDefaultMessage());
        }

        if (Tools.isEmpty(img1Base64) && Tools.isEmpty(img2Base64))
        {
            return ResultUtil.fail(-1, "请上传广告位图片");
        }

        if (product.getPriceType() == 0)
        {
            if (Tools.isEmpty(cycles) || Tools.isEmpty(cyclePrice))
            {
                return ResultUtil.fail(-1, "请输入周期价格");
            }
        }
        else
        {
            bidParameter.setStartTime(Tools.str2Date(startTimeParam));
            bidParameter.setEndTime(Tools.str2Date(endTimeParam));
            bidParameter.setDeliverTime(Tools.str2Date(deliverTimeParam));
            // 竞价时间校验
            // 开始时间
            Date startTime = bidParameter.getStartTime();
            // 结束时间
            Date endTime = bidParameter.getEndTime();
            // 广告位交付 时间
            Date deliverTime = bidParameter.getDeliverTime();

            if (startTime.after(endTime))
            {
                return ResultUtil.fail(-1, "请设置竞价正确的起止时间");
            }

            if (deliverTime.before(endTime))
            {
                return ResultUtil.fail(-1, "请设置正确的广告位交付时间");
            }
        }

        // 保存图片
        Date date = new Date();
        String img1Path = "";
        String img2Path = "";
        if (!Tools.isEmpty(img1Base64))
        {
            String img1Name = "product_" + date.getTime() + Tools.randomFourDigit()+ ".jpg";
            img1Path = Tools.uploadImg(img1Base64, img1Name);
            if (img1Path.equals("error"))
            {
                return ResultUtil.fail(-1, "广告位图片上传失败");
            }
        }

        if (!Tools.isEmpty(img2Base64))
        {
            String img2Name = "product_" + date.getTime() + Tools.randomFourDigit()+ ".jpg";
            img2Path = Tools.uploadImg(img2Base64, img2Name);
            if (img2Path.equals("error"))
            {
                return ResultUtil.fail(-1, "广告位图片上传失败");
            }
        }

        // 保存
        // product = productService.save(product, bidParameter, productAttribute, cycles,cyclePrice, img1Path, img2Path, vendorId);

        return ResultUtil.success(product);
    }

    /**
     * 可以购买的四个时间段
     * @return
     */
    @GetMapping(value="/product/period/enable")
    public Result getEnableBuyPeriods()
    {
        List<String> periods = Tools.getBuyPeriod();

        return ResultUtil.success(periods);
    }

    /**
     * 查询站点下所有广告位
     * @param positionId
     * @return
     */
    @GetMapping(value="/product/position/{positionId}")
    public Result<Product> findByPositionId(@PathVariable("positionId") Integer positionId)
    {
        List<Product> products = productService.findByPositionId(positionId);
        return ResultUtil.success(products);
    }

    @GetMapping(value="/product/{id}")
    public Result<Product> findById(@PathVariable("id") Integer id)
    {
        return ResultUtil.success(productService.findById(id));
    }

    /**
     * 地图查询页面 查询所有站点
     * @param area
     * @param vendorId
     * @return
     */
    @PostMapping(value="/product/position/search")
    public Result<Position> positionSearch(@RequestParam("area")String area,
                                           @RequestParam("vendorId")Integer vendorId)
    {
        final String searchArea = area;
        final Integer searchVendorId = vendorId;

        Specification<Position> specification = new Specification<Position>()
        {
            @Override
            public Predicate toPredicate(Root<Position> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();

                if (!"全城".equals(searchArea))
                {
                    predicate.add(criteriaBuilder.equal(root.get("area").as(String.class), searchArea));
                }

                if (searchVendorId != 0)
                {
                    predicate.add(criteriaBuilder.equal(root.get("vendorId").as(Integer.class), searchVendorId));
                }

                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };

        List<Position> positions = positionService.findAll(specification);
        return ResultUtil.success(positions);
    }


//    @GetMapping(value="/product")
//    public Result<Product> findAll()
//    {
//        return ResultUtil.success(productService.findByPriceType(0));
//    }

    /**
     * 热门广告位（前3条）
     * @return
     */
    @GetMapping(value="/product/hot/{vendorId}")
    public Result<Product> hotProduct(@PathVariable("vendorId")Integer vendorId)
    {
        return ResultUtil.success(positionService.hotPosition(vendorId));
    }

    /**
     * 所有广告位
     * @param page
     * @return
     */
    @PostMapping("/product/list")
    public Result<Position> findAll(@RequestParam("page")Integer page,
                                   @RequestParam("vendorId")Integer vendorId)
    {
        Pageable pageable = new PageRequest(page,10, Sort.Direction.ASC,"id");
        final Integer finalVendorId = vendorId;
        Specification<Position> specification = new Specification<Position>(){

            @Override
            public Predicate toPredicate(Root<Position> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicate = new ArrayList<>();
                // account
                if (finalVendorId != 0)
                {
                    // 查询指定资源方的广告位
                    predicate.add(criteriaBuilder.equal(root.get("vendorId").as(Integer.class), finalVendorId));
                }

                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return ResultUtil.success(positionService.findAll(specification, pageable));
    }

    /**
     * 查询所有竞价广告位（前台用）
     * @return
     */
    @GetMapping(value="/product/bid/{vendorId}")
    public Result<Product> findBidProduct(@PathVariable("vendorId")Integer vendorId)
    {
       List<Product> products = productService.findByPriceType(1);
       List<Product> resultProducts = new ArrayList<Product>();
       for (int i = 0; i < products.size(); i ++)
       {
           if (vendorId != 0)
           {
               if (products.get(i).getPosition().getVendorId() != vendorId)
               {
                   continue;
               }
           }
           Date nowTime = new Date();
           Date startTime = products.get(i).getBidParameter().getStartTime();
           Date endTime = products.get(i).getBidParameter().getEndTime();
           if (endTime.after(nowTime))
           {
               if (startTime.before(nowTime))
               {
                   // 竞价已经开始
                   long timeDiff = (endTime.getTime() - nowTime.getTime()) / 1000;
                   products.get(i).getBidParameter().setDiffTime(timeDiff);
                   products.get(i).getBidParameter().setDiffTimeType(0);
               }
               else if (startTime.after(nowTime))
               {
                   // 竞价未开始
                   long timeDiff = (startTime.getTime() - nowTime.getTime()) / 1000;
                   products.get(i).getBidParameter().setDiffTime(timeDiff);
                   products.get(i).getBidParameter().setDiffTimeType(1);
               }
               resultProducts.add(products.get(i));
           }
       }

       return ResultUtil.success(resultProducts);
    }

    @GetMapping(value="/product/vendor/{accountId}")
    public Result<Product> findByAccountId(@PathVariable("accountId")Integer accountId)
    {
        return ResultUtil.success(productService.findByAccountId(accountId));
    }

    /**
     * 前台地图查询用
     * @param productType
     * @param productArea
     * @return
     */
    @PostMapping(value="/product/search")
    public Result<Product> search(@RequestParam("productType")String productType,
                                 @RequestParam("productArea")String productArea,
                                  @RequestParam("vendorId")Integer vendorId)
    {
        final String type = productType;
        final String area = productArea;
        final Integer finalVendorId = vendorId;

        Specification<Product> specification = new Specification<Product>(){

            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();
                if (!Tools.isEmpty(type))
                {
                    predicate.add(criteriaBuilder.equal(root.get("productType").as(String.class), type));
                }
                if (!Tools.isEmpty(area) && !"全城".equals(area))
                {
                    predicate.add(criteriaBuilder.equal(root.get("area").as(String.class), area));
                }
                if (finalVendorId != 0)
                {
                    predicate.add(criteriaBuilder.equal(root.get("accountId").as(Integer.class), finalVendorId));
                }
                // 价格类型
                predicate.add(criteriaBuilder.equal(root.get("priceType").as(Integer.class), 0));
                // 公商类型
                predicate.add(criteriaBuilder.equal(root.get("useType").as(String.class), "商业类"));
                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return ResultUtil.success(productService.findAll(specification));
    }

    @GetMapping(value="/product/{productType}/{area}")
    public Result<Product> findByParam(@PathVariable("productType")String productType,
                                       @PathVariable("area")String area)
    {
        try {
            productType = URLDecoder.decode(productType, "UTF-8");
            area = URLDecoder.decode(area, "UTF-8");

            if ("全城".equals(area))
            {
                return ResultUtil.success(productService.findByProductType(productType));
            }
            else
            {
                // return ResultUtil.success(productService.findByProductTypeAndArea(productType, area));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ResultUtil.success();
    }

    @GetMapping(value="/server/time/{productId}")
    public Result<BidDiffTime> getServerTime(@PathVariable("productId")Integer productId)
    {
        // 获取产品信息
        Product product= productService.findById(productId);

        BidParameter bidParameter = product.getBidParameter();

        // 竞价开始时间
        Date startTime = bidParameter.getStartTime();
        // 竞价结束时间
        Date endTime = bidParameter.getEndTime();
        // 当前时间
        Date nowTime = new Date();
        long startTimeDiff = 0;
        long endTimeDiff = 0;
        BidDiffTime bidDiffTime = new BidDiffTime();

        if (nowTime.before(startTime))
        {
            // 竞价未开始
            startTimeDiff = (startTime.getTime() - nowTime.getTime()) / 1000;
            endTimeDiff = (endTime.getTime() - startTime.getTime()) / 1000;
            log.info("竞价未开始：距离{}开始", startTimeDiff);
            bidDiffTime.setType(0);
        }
        else if (nowTime.after(endTime))
        {
            // 竞价已经结束
            bidDiffTime.setType(2);
            log.info("竞价已结束");
        }
        else
        {
            // 竞价中
            endTimeDiff = (endTime.getTime() - nowTime.getTime()) / 1000;
            log.info("竞价已开始，剩余时间{}", endTimeDiff);
            bidDiffTime.setType(1);
        }
        bidDiffTime.setEndDiffTime(endTimeDiff);
        bidDiffTime.setStartDiffTime(startTimeDiff);
        return ResultUtil.success(bidDiffTime);
    }

    @GetMapping(value="/product/user")
    public Result<Product> findByAccountId(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);

        return ResultUtil.success(productService.findByAccountId(accountId));
    }

//    @GetMapping(value="/product/priceType/{priceType}")
//    public Result<Product> findByAccountIdAndPriceType(@PathVariable("priceType") Integer priceType,
//                                                       HttpServletRequest request)
//    {
//        Integer vendorId = Tools.getVendorId(request);
//        return ResultUtil.success(productService.findByAccountIdAndPriceType(vendorId, priceType));
//    }

    /**
     * 判断是否有权限发布广告位
     * @param request
     * @return
     */
    @GetMapping(value="/product/publish/validate")
    public Result validatePublish(HttpServletRequest request)
    {
        Integer accountId = Tools.getUserId(request);
        Integer vendorId = findVendorId(accountId);
        if (vendorId == 0)
        {
            return ResultUtil.fail(-1,"暂时无权限发布广告位，请通过资源方审核后在进行发布");
        }
        else
        {
            return ResultUtil.success();
        }
    }

    /**
     * 查询产品的所有排期
     * @param productId
     * @return
     */
    @GetMapping(value="/product/period/{productId}")
    public Result productPeriod(@PathVariable("productId")Integer productId)
    {
        List<ProductPeriod> productPeriods = productPeriodService.findByProductId(productId);

        return ResultUtil.success(productPeriods);
    }

    /**
     * 根据登录用户id获取vendorId
     * @param accountId
     * @return
     */
    public Integer findVendorId(Integer accountId)
    {
        Integer vendorId = 0;

        // 判断是否是企业直接添加信息
        Enterprise enterprise = enterpriseService.getByAccountId(accountId);
        if (enterprise == null)
        {
            // 判断是否是员工
            String mobileNo = accountService.findById(accountId).getMobileNo();
            Employee employee = employeeService.findByMobileNo(mobileNo);
            if (employee == null)
            {
                // 无权限发布广告位
                log.info("无权限发布广告位");
            }
            else
            {
                // 企业ID
                Integer departmentId = employee.getDepartment().getId();
                // 获取部门信息
                Department department = departmentService.findById(departmentId);
                // 企业ID
                Integer enterpriseId = department.getEnterprise().getId();
                // 获取企业信息
                Enterprise enterprise1 = enterpriseService.getById(enterpriseId);

                vendorId = enterprise1.getAccountId();
            }
        }
        else
        {
            Integer state = enterprise.getState();
            if (state == 1)
            {
                vendorId = enterprise.getAccountId();
            }
            else
            {
                log.info("无权发布广告位");
            }
        }

        return vendorId;
    }

    /**
     * 查询点位被占用档期（达美用）
     * @param pointId
     * @return
     */
    @GetMapping(value="/product/unavaiTime/{pointId}")
    public Result getUnavaiTime(@PathVariable("pointId")Integer pointId)
    {
        String pointIdStr = pointId + "";
        String url = "http://dm.msplat.cn/api/point/unavaiTime.htm";
        List<NameValuePair> paramsList = new ArrayList();
        paramsList.add(new BasicNameValuePair("key", "112B41FA2DF34815983FC5DD82831EFC"));
        paramsList.add(new BasicNameValuePair("id", pointIdStr));

        JSONObject jsonObject = WebUtil.post2(url, paramsList);
        JSONArray data = (JSONArray)jsonObject.get("data");

        return ResultUtil.success(data);
    }
}
