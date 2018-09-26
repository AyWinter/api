package com.mash.api.service.impl;

import com.mash.api.entity.Bond;
import com.mash.api.entity.MainOrder;
import com.mash.api.entity.Product;
import com.mash.api.entity.Refund;
import com.mash.api.repository.RefundRepository;
import com.mash.api.service.BondService;
import com.mash.api.service.OrderService;
import com.mash.api.service.ProductService;
import com.mash.api.service.RefundService;
import com.mash.api.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private BondService bondService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Override
    public List<Refund> findByVendorId(Integer vendorId) {
        return refundRepository.findByVendorId(vendorId);
    }

    @Override
    public void updateRefundState(Integer id, String operator, String explainText, Date operateTime, Integer state) {

        refundRepository.updateRefundState(id, operator, explainText, operateTime, state);
    }

    @Override
    public Refund save(Refund refund) {
        return refundRepository.save(refund);
    }

    @Transactional
    @Override
    public Refund refundApply(String orderNo, Integer productId, HttpServletRequest request) {

        Integer accountId = Tools.getUserId(request);
        Refund refund = new Refund();

        // 获取保证金信息
        Bond bond = bondService.findByAccountIdAndProductId(accountId, productId);
        // 获取产品信息
        Product product = productService.findById(productId);
        // 获取订单信息
        MainOrder order = orderService.findByOrderNo(orderNo);

        refund.setVendorId(product.getAccountId());
        refund.setOrder(order);
        refund.setState(1);
        refund.setApplyTime(new Date());
        // 保存退款申请
        refund = refundRepository.save(refund);

        // 更新订单退款状态
        orderService.updateRefundStateToExamine(orderNo);

        return refund;
    }
}
