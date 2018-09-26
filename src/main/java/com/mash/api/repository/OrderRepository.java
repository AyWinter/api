package com.mash.api.repository;

import com.mash.api.entity.MainOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<MainOrder, Integer>{

    MainOrder findByOrderNo(String orderNo);

    /**
     * 查询用户所有订单
     * @param accountId
     * @param type
     * @return
     */
    List<MainOrder> findByAccountIdAndTypeAndState(Integer accountId, Integer type, Integer payState);

    /**
     *
     * @param accountId
     * @param type
     * @return
     */
    List<MainOrder> findByAccountIdAndType(Integer accountId, Integer type);

    /**
     * 根据状态查询用户所有订单
     * @param accountId
     * @param state
     * @return
     */
    List<MainOrder> findByAccountIdAndState(Integer accountId, Integer state);

    /**
     * 支付成功后更新订单信息
     * @param state
     * @param payMethod
     * @param payTime
     * @param transactionId
     * @param orderNo
     */
    @Transactional
    @Query(value="update main_order set state=?1, pay_method=?2, pay_time=?3, transaction_id=?4 where order_no=?5", nativeQuery = true)
    @Modifying
    void updateOrderState(Integer state, Integer payMethod, Date payTime, String transactionId, String orderNo);

    @Transactional
    @Query(value="update main_order set refund_amount=?4, refund_no=?2, refund_state=1, refund_time=?3 where order_no=?1", nativeQuery = true)
    @Modifying
    void updateRefundState(String orderNo, String refundNo, Date refundTime, float refundAmount);

    @Transactional
    @Query(value="update main_order set refund_state=2 where order_no=?1", nativeQuery = true)
    @Modifying
    void updateRefundStateToExamine(String orderNo);

    /**
     * 获取用户带退款的保证金记录
     * @param accountId
     * @return
     */
    @Query(value="select * from main_order where type=2 and account_id=?1 and refund_state is null", nativeQuery = true)
    List<MainOrder> findRefundBondOrder(Integer accountId);


    /**
     * 后台系统查询所有订单
     * @param specification
     * @param pageable
     * @return
     */
    Page<MainOrder> findAll(Specification<MainOrder> specification, Pageable pageable);


    @Transactional
    @Query(value="update main_order set state=3 where order_no=?1", nativeQuery = true)
    @Modifying
    void cancel(String orderNo);

    @Query(value="select * from main_order where state != 3 and state != 1 and type = 1", nativeQuery = true)
    List<MainOrder> findNotCancel();

    /**
     * 退款申请
     * @param reason
     * @param applyTime
     * @param orderNo
     */
    @Transactional
    @Query(value="update main_order set refund_state=2, refund_reason=?1, apply_time=?2 where order_no=?3", nativeQuery = true)
    @Modifying
    void refundApply(String reason, Date applyTime, String orderNo);
}
