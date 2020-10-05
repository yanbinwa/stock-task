package com.yanbin.stock.stocktaskutils.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:07
 *
 * 加载了一只股票某一天的实时数据
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTs {
    
    List<StockTsElement> elements;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StockTsElement {
        private String time;
        private Double price;
        private Double volume;
        private Double avgPrice;
        private Double turnover;
    }
}
