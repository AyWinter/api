package com.mash.api.service.impl;

import com.mash.api.entity.BidRecord;
import com.mash.api.repository.BidRecordRepository;
import com.mash.api.service.BidRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BidRecordServiceImpl implements BidRecordService {

    @Autowired
    private BidRecordRepository bidRecordRepository;

    @Transactional
    @Override
    public BidRecord save(BidRecord bidRecord) {

        // 保存出价记录
        bidRecord = bidRecordRepository.save(bidRecord);
        // 更新当前产品的其他出价记录
        bidRecordRepository.updateBidRecordState(bidRecord.getProduct().getId(), bidRecord.getId());

        return bidRecord;
    }

    @Override
    public BidRecord findHighestPrice(Integer productId) {
        return bidRecordRepository.findHighestPrice(productId);
    }

    @Override
    public List<BidRecord> findByProductId(Integer productId) {
        return bidRecordRepository.findByProductId(productId);
    }
}
