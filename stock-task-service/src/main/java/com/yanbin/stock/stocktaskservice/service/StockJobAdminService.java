package com.yanbin.stock.stocktaskservice.service;

import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:43
 */
public interface StockJobAdminService {
    void runStockJob(StockJob stockJob);
    void scheduleStockAction(StockActionExecuteMsg stockActionExecuteMsg);
}
