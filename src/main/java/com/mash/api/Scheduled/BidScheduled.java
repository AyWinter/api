package com.mash.api.Scheduled;

import com.mash.api.entity.BidParameter;
import com.mash.api.entity.BidRecord;
import com.mash.api.entity.BidResult;
import com.mash.api.entity.Product;
import com.mash.api.service.BidRecordService;
import com.mash.api.service.BidResultService;
import com.mash.api.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class BidScheduled {

    @Autowired
    private ProductService productService;

    @Autowired
    private BidRecordService bidRecordService;

    @Autowired
    private BidResultService bidResultService;

    private static final Logger log = LoggerFactory.getLogger(BidScheduled.class);

    @Scheduled(fixedRate = 1000)
    public void timerRate() {

        List<Product> productList = productService.findByPriceType(1);

        for (Product product : productList)
        {
            BidResult bidResult = bidResultService.findByProductId(product.getId());
            if (bidResult != null)
            {
                continue;
            }

            BidParameter bidParameter = product.getBidParameter();

            // 竞价结束时间
            Date endTime = bidParameter.getEndTime();
            // 当前时间
            Date currentTime = new Date();

            if (currentTime.after(endTime) || currentTime.getTime() == endTime.getTime())
            {
                BidRecord bidRecord = bidRecordService.findHighestPrice(product.getId());

                if (bidRecord != null)
                {
                    bidResult = new BidResult();
                    bidResult.setAccountId(bidRecord.getAccountId());
                    bidResult.setMobileNo(bidRecord.getMobileNo());
                    bidResult.setAmount(bidRecord.getAmount());
                    bidResult.setState(0);
                    bidResult.setProduct(product);
                    bidResultService.save(bidResult);
                }

            }
        }
    }
}
