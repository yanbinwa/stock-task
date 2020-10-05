package com.yanbin.stock.stocktaskservice.service;

import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;

import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:56
 *
 * 股票任务管理
 */
public interface StockJobManagerService {
    StockJob addStockJob(String appId, StockJob stockJob);
    StockJob getStockJob(String appId, Integer id);
    StockJob updateStockJob(String appId, Integer id, StockJob stockJob) throws StockTaskException;
    List<StockJob> getAllStockJob(String appId);
    void runStockJob(String appId, Integer id) throws StockTaskException;
}
