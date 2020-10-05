package com.yanbin.stock.stocktaskutils.constants;

import lombok.Getter;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:52
 */
@Getter
public enum StockTaskResponseInfo {

    STOCK_TASK_JOB_NOT_EXIST(6101, "任务不存在"),
    TIME_FORMAT_ILLEGAL(6102, "输入时间异常"),
    HOLIDAY_FETCH_FAILED(6103, "节假日获取失败"),
    ;

    private Integer code;
    private String message;

    StockTaskResponseInfo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
