package com.yanbin.stock.stocktaskutils.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:07
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private Integer id;

    private String code;

    private String name;

    private String url;

    private Double price;

    private Double growthRate;

    private Double growth;

    private Double exchange;

    private Double volumeRate;

    private Double amplitude;

    private Double turnover;

    private Double market;

    private Double markWin;
}
