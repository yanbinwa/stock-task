package com.yanbin.stock.stocktaskutils.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/29 上午8:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockIndustryRequest extends StockQueryRequest {
    private Integer industrySize;
}
