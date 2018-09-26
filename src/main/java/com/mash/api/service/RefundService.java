package com.mash.api.service;

import com.mash.api.entity.Refund;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public interface RefundService {

    List<Refund> findByVendorId(Integer vendorId);

    void updateRefundState(Integer id, String operator, String explainText, Date operateTime, Integer state);

    Refund save(Refund refund);

    Refund refundApply(String orderNo, Integer productId, HttpServletRequest request);
}
