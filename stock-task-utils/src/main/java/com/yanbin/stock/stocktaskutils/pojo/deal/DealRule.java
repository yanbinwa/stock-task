package com.yanbin.stock.stocktaskutils.pojo.deal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/29 上午10:29
 *
 * 1. 交易的时间, 可以是指定的time（9点32分，具体执行的时间由发生时为准）
 * 2. 延后交易的天数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealRule {
    private String time;
    // 需要计算交易时间，是工作日，去除节假日，可以有一个服务来专门计算（或者爬取一只股票一年的数据，来判断开市的时间）
    private Integer offset;
}
