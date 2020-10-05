package com.yanbin.stock.stocktaskservice.utils;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.gemini.geminiutils.utils.JsonUtils;
import com.yanbin.stock.stocktaskservice.annotation.StockExecutor;
import com.yanbin.stock.stocktaskservice.executor.IStockExecutor;
import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:29
 *
 * 发布子action
 * 1. 及时生效，只执行一次
 * 2. 定时生效，通过时间间隔
 * 3. 周期生效（在创建action时才会用到，一般是不会的）
 */
@Slf4j
@Component
public class StockActionHelper {

    @Autowired
    ApplicationContext applicationContext;

    // action没有嵌套，所以比较简单
    public StockAction buildStockAction(StockActionType stockActionType, JSONObject jsonObject) {
        try {
            Class<? extends StockAction> actionClazz = stockActionType.getClazz();
            if (jsonObject == null) {
                Constructor con = actionClazz.getConstructor();
                return (StockAction) con.newInstance();
            } else {
                StockAction action = JsonUtils.copyObject(jsonObject, actionClazz);
                action.setActionType(stockActionType);
                return action;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Boolean executeAction(StockAction stockAction, StockActionExecuteMsg stockActionExecuteMsg) {
        IStockExecutor stockExecutor = fetchStockExecutor(stockAction.getActionType());
        if (stockExecutor == null) {
            log.error("stock executor not find");
            return false;
        }
        return stockExecutor.executeAction(stockAction, stockActionExecuteMsg);
    }

    public IStockExecutor fetchStockExecutor(StockActionType stockActionType) {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(StockExecutor.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            IStockExecutor actionExecutor = (IStockExecutor) entry.getValue();
            StockExecutor annotation = actionExecutor.getClass().getAnnotation(StockExecutor.class);
            if (annotation.type().equals(stockActionType)) {
                return actionExecutor;
            }
        }
        return null;
    }
}
