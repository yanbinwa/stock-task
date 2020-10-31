package com.yanbin.stock.stocktaskutils.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/27 上午7:44
 *
 * 行业信息
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Industry {
    private String name;
    private Double growthRate;
    private Double netInflow;
}
