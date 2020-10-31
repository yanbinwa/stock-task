package com.yanbin.stock.stocktaskutils.pojo.request;

import com.yanbin.stock.stocktaskutils.pojo.deal.DealRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/3 下午5:58
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTestRequest extends StockQueryRequest {

    // 回测开始时间
    private String startTime;
    // 回测结束时间
    private String endTime;
    // 买入规则
    protected DealRule buyRule;
    // 卖出规则
    protected DealRule saleRule;

}
