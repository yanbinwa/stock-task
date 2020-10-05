package com.yanbin.stock.stocktaskutils.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/3 下午6:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTestResult {
    // 回测日期
    private String date;
    // 股票名称
    private String name;
    // 股票编码
    private String code;
    // 购买时间（日期 + 时间）
    private String buyTime;
    private Double buyPrice;
    // 卖出时间（日期 + 时间）
    private String saleTime;
    private Double salePrice;
    // 总收益，按百分比
    private Double income;
}
