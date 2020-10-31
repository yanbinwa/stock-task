package com.yanbin.stock.stocktaskutils.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/29 上午8:16
 *
 * 需要将行情数据加入
 *
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockQueryRequest {

    protected String queryTemplate;

    protected List<String> queryTimes;

}
