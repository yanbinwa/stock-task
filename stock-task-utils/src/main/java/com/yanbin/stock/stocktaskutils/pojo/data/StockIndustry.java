package com.yanbin.stock.stocktaskutils.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/30 上午8:00
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockIndustry extends Stock {
    private Integer industryOrder;
    private String industryName;
    private Double industryGrowthRate;
    private Double industryNetInflow;
}
