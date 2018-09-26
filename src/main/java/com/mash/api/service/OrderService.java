package com.mash.api.service;

import com.mash.api.entity.*;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;

public interface OrderService {

    MainOrder save(MainOrder mainOrder, String name, String phone, List<Shopcart> shopcarts, String scheduleNumber);

    /**
     * 缴纳竞价保证金订单做成
     *
     * @param mainOrder
     * @param bond
     * @return
     */
    MainOrder bondSave(MainOrder mainOrder, Bond bond);

    MainOrder findById(Integer id);

    MainOrder findByOrderNo(String orderNo);

    /**
     * 用户中心， 我的订单
     * @param accountId
     * @param type
     * @param payState
     * @return
     */
    List<MainOrder> findByAccountIdAndTypeAndState(Integer accountId, Integer type, Integer payState);

    /**
     * 用户中心，我的所有订单
     * @param accountId
     * @param type
     * @return
     */
    List<MainOrder> findByAccountIdAndType(Integer accountId, Integer type);

    /**
     * 获取用户带退款的保证金记录
     * @param accountId
     * @return
     */
    List<MainOrder> findRefundBondOrder(Integer accountId);

    List<MainOrder> findByAccountIdAndState(Integer accountId, Integer state);

    List<MainOrder> findAll();

    /**
     * 支付成功后更新订单信息
     *
     * @param state
     * @param payMethod
     * @param payTime
     * @param transactionId
     * @param orderNo
     * @param type
     * @param id
     */
    void updateOrderState(Integer state, Integer payMethod, Date payTime, String transactionId, String orderNo, Integer type, Integer id);

    /**
     * 退款后更新订单信息
     * @param orderNo
     * @param refundNo
     */
    void updateRefundState(String orderNo, String refundNo, Date refundTime, float refundAmount, Integer refundId, String operator, String explainText);

    /**
     * 退款申请提交后，更新退款状态为审核中
     * @param orderNo
     */
    void updateRefundStateToExamine(String orderNo);

    MainOrder findByOrderProductId(Integer orderProductId);

    /**
     * 后台系统查询所有订单
     * @param specification
     * @param pageable
     * @return
     */
    Page<MainOrder> findAll(Specification<MainOrder> specification, Pageable pageable);

    /**
     * 取消订单
     * @param orderNo
     */
    void cancel(String orderNo);

    /**
     * 查询未关闭的订单
     * @return
     */
    List<MainOrder> findNotCancel();

    /**
     * 退款申请
     * @param reason
     * @param applyTime
     * @param orderNo
     */
    void refundApply(String reason, Date applyTime, String orderNo);
}
