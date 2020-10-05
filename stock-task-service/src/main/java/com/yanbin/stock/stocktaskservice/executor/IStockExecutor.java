package com.yanbin.stock.stocktaskservice.executor;

import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:13
 *
 * 会执行一个action，并将内部生成的action
 */
public interface IStockExecutor {
    Boolean executeAction(StockAction stockAction, StockActionExecuteMsg stockActionExecuteMsg);
}
