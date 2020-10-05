package com.yanbin.stock.stocktaskservice.service;

import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.request.StockTestRequest;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/3 下午5:56
 *
 * 输入一个请求，返回一个excel
 */
public interface StockTestService {
    void regressionTest(StockTestRequest stockTestRequest) throws StockTaskException;
}