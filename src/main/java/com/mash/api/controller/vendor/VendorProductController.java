package com.mash.api.controller.vendor;

import com.mash.api.entity.*;
import com.mash.api.repository.ProductPeriodRepository;
import com.mash.api.service.*;
import com.mash.api.util.ResultUtil;
import com.mash.api.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@RestController
public class VendorProductController {

    private static final Logger log = LoggerFactory.getLogger(VendorProductController.class);

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPeriodRepository productPeriodRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

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
    @PostMapping(value="/vendor/product")
    public Result<Product> save(@Valid Product product,
                                ProductAttribute productAttribute,
                                BindingResult bindingResult,
                                BidParameter bidParameter,
                                @RequestParam("img1Base64") String img1Base64,
                                @RequestParam("img2Base64") String img2Base64,
                                @RequestParam("cycles") String cycles,
                                @RequestParam("cyclePrice") String cyclePrice,
                                @RequestParam("startTimeParam") String startTimeParam,
                                @RequestParam("endTimeParam") String endTimeParam,
                                @RequestParam("deliverTimeParam") String deliverTimeParam,
                                @RequestParam("positionId")Integer positionId,
                                HttpServletRequest request)
    {
        // 获取vendorId
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);
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
        product = productService.save(product, bidParameter, productAttribute, cycles,cyclePrice, img1Path, img2Path, vendorId, positionId);

        return ResultUtil.success(product);
    }

    /**
     * 条件查询
     * @param page
     * @param pageSize
     * @param area
     * @param level
     * @param startTime
     * @param endTime
     * @return
     */
    @PostMapping(value="/vendor/product/search")
    public Result<Product> search(@RequestParam("page")Integer page,
                                  @RequestParam("pageSize")Integer pageSize,
                                  @RequestParam("area")String area,
                                  @RequestParam("level")String level,
                                  @RequestParam("startTime")String startTime,
                                  @RequestParam("endTime")String endTime,
                                  HttpServletRequest request)
    {

        Pageable pageable = new PageRequest(page,pageSize, Sort.Direction.DESC,"id");
        final String searchLevel = level;
        final Date searchStartTime = startTime != "" ? Tools.str2Date(startTime + " 00:00:00") : null;
        final Date searchEndTime = endTime != "" ? Tools.str2Date(endTime + " 23:59:59") : null;
        final String searchArea = area;

        // 获取vendorId
        final Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        // 获取所有排期
        List<Integer> ids = new ArrayList<Integer>();
        List<ProductPeriod> productPeriods = productPeriodRepository.findAll();
        for (ProductPeriod productPeriod : productPeriods)
        {
            if (searchStartTime != null && searchEndTime != null)
            {
                boolean check = Tools.dateCheckOverlap(searchStartTime, searchEndTime, productPeriod.getStartTime(), productPeriod.getEndTime());
                if (check)
                {
                    // 所选时段被占用
                    Integer productId = productPeriod.getProduct().getId();
                    log.info("productId = {}", productId);
                    if (!ids.contains(productId))
                    {
                        ids.add(productId);
                    }
                }
            }
        }
        // 当前时段被占用的产品ID
        final List<Integer> productIds = ids;

        // 查询所有产品ID
        final List<Integer> allIds = productService.findProductId();
        // 将占用的产品id清除掉
        allIds.removeAll(productIds);

        // 区域查询
        final List<Integer> positionIds = positionService.findPositionIdsByVendorIdAndArea(vendorId, area);
        if(positionIds.size() == 0)
        {
            positionIds.add(-1);
        }

        Specification<Product> specification = new Specification<Product>(){

            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicate = new ArrayList<>();

                predicate.add(criteriaBuilder.equal(root.get("accountId").as(Integer.class), vendorId));

                if(!Tools.isEmpty(searchLevel) && !"全部".equals(searchLevel))
                {
                    predicate.add(criteriaBuilder.equal(root.get("level").as(String.class), searchLevel));
                }
                // 价格类型
                predicate.add(criteriaBuilder.equal(root.get("priceType").as(Integer.class), 0));
                if (allIds.size() > 0)
                {
                    predicate.add(root.<Integer>get("id").in(allIds));
                }

                // 区域
                if(!Tools.isEmpty(searchArea) && !"全城".equals(searchArea))
                {
                    predicate.add(root.get("position").<Integer>get("id").in(positionIds));
                }

                Predicate[] pre = new Predicate[predicate.size()];
                return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
            }
        };
        Page<Product> productPage = productService.findAll(specification, pageable);
        return ResultUtil.success(productPage);
    }

    /**
     * 资源列表
     * @param pageNo
     * @param request
     * @return
     */
    @GetMapping(value="/vendor/product/page/{pageNo}")
    public Result<Product> page(@PathVariable("pageNo")Integer pageNo,
                                HttpServletRequest request)
    {
        // 获取vendorId
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);

        Pageable pageable = new PageRequest(pageNo,10, Sort.Direction.DESC,"id");
        return ResultUtil.success(productService.findByAccountIdAndPriceType(pageable, vendorId, 0));
    }

    @GetMapping(value="/vendor/productId")
    public Result test()
    {
      return ResultUtil.success(productService.findProductId());
    }


    @GetMapping(value="/vendor/product/page/{priceType}/{pageNo}")
    public Result<Product> findPageByAccountIdAndPriceType(@PathVariable("priceType") Integer priceType,
                                                           @PathVariable("pageNo")Integer pageNo,
                                                           HttpServletRequest request)
    {
        // 获取vendorId
        Integer vendorId = Tools.getVendorId(request,
                enterpriseService,
                accountService,
                employeeService,
                departmentService);
        Pageable pageable = new PageRequest(pageNo,10, Sort.Direction.DESC,"id");
        return ResultUtil.success(productService.findByAccountIdAndPriceType(pageable, vendorId, priceType));
    }

    @PostMapping(value="/vendor/product/image")
    public Result uploadImage(HttpServletRequest request)throws Exception
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //文件对象
        Map<String, MultipartFile> files = multipartRequest.getFileMap();

        MultipartFile multipartFile = files.get("file");
        InputStream in = multipartFile.getInputStream();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = in.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        in.close();

        byte[] bytes = outStream.toByteArray();
        String strBase64 = new BASE64Encoder().encode(bytes);

        return ResultUtil.success("data:image/jpeg;base64," + strBase64);
    }

    @GetMapping(value="/vendor/product/{id}")
    public Result<Product> findById(@PathVariable("id")Integer id)
    {
        return ResultUtil.success(productService.findById(id));
    }
}
