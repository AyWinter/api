package com.mash.api.service;

import com.mash.api.entity.BidRecord;

import java.util.List;

public interface BidRecordService {

    BidRecord save(BidRecord bidRecord);

    /**
     * 获取广告位当前价格
     * @param productId
     * @return
     */
    BidRecord findHighestPrice(Integer productId);

    List<BidRecord> findByProductId(Integer productId);
}
