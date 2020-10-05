package com.yanbin.stock.stocktaskservice.executor;

import com.yanbin.stock.stocktaskservice.service.StockJobAdminService;
import com.yanbin.stock.stocktaskservice.utils.StockActionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:31
 */
@Component
public abstract class BaseStockExecutor implements IStockExecutor {
    @Autowired
    protected StockActionHelper stockActionHelper;

    @Autowired
    protected StockJobAdminService stockJobAdminService;
}
