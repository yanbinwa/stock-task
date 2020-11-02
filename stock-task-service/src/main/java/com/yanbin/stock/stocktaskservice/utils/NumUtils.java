package com.yanbin.stock.stocktaskservice.utils;

import java.text.DecimalFormat;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/11/2 上午8:03
 */
public class NumUtils {

    private static DecimalFormat df = new DecimalFormat("#0.00");

    /**
     * 1. 乘以100
     * 2. 保留两位小数点
     * 3. 加一个百分号
     *
     * @param value
     * @return
     */
    public static String buildPercentageNum(Double value) {
        value *= 100;
        return df.format(value) + "%";
    }

    public static void main(String[] args) {
        System.out.println(buildPercentageNum(0.00134112));
    }
}
