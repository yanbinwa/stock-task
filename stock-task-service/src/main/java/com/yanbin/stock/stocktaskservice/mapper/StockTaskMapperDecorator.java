package com.yanbin.stock.stocktaskservice.mapper;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.gemini.geminiutils.utils.JsonUtils;
import com.yanbin.stock.stocktaskservice.entity.StockActionExecuteMsgEntity;
import com.yanbin.stock.stocktaskservice.entity.StockJobEntity;
import com.yanbin.stock.stocktaskutils.pojo.StockActionContext;
import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:31
 */
public abstract class StockTaskMapperDecorator implements StockTaskMapper {

    @Autowired
    StockTaskMapper stockTaskMapper;

    @Override
    public StockJobEntity stockJobToStockJobEntity(StockJob stockJob) {
        StockJobEntity stockJobEntity = stockTaskMapper.stockJobToStockJobEntity(stockJob);
        if (stockJob.getStockAction() != null) {
            stockJobEntity.setActionStr(JsonUtils.getStrFromObject(stockJob.getStockAction()));
        }
        return stockJobEntity;
    }

    @Override
    public StockJob stockJobEntityToStockJob(StockJobEntity stockJobEntity) {
        StockJob stockJob = stockTaskMapper.stockJobEntityToStockJob(stockJobEntity);
        if (!StringUtils.isEmpty(stockJobEntity.getActionStr())) {
            stockJob.setStockAction(JsonUtils.getObjectFromStr(stockJobEntity.getActionStr(), JSONObject.class));
        }
        return stockJob;
    }

    @Override
    public StockActionExecuteMsgEntity stockActionExecuteMsgToStockActionExecuteMsgEntity(StockActionExecuteMsg stockActionExecuteMsg) {
        StockActionExecuteMsgEntity stockActionExecuteMsgEntity =
                stockTaskMapper.stockActionExecuteMsgToStockActionExecuteMsgEntity(stockActionExecuteMsg);
        if (stockActionExecuteMsg.getAction() != null) {
            stockActionExecuteMsgEntity.setActionStr(JsonUtils.getStrFromObject(stockActionExecuteMsg.getAction()));
        }
        if (stockActionExecuteMsg.getContext() != null) {
            stockActionExecuteMsgEntity.setActionContextStr(JsonUtils.getStrFromObject(stockActionExecuteMsg.getContext()));
        }
        return stockActionExecuteMsgEntity;
    }

    @Override
    public StockActionExecuteMsg stockActionExecuteMsgEntityToStockActionExecuteMsg(StockActionExecuteMsgEntity stockActionExecuteMsgEntity) {
        StockActionExecuteMsg stockActionExecuteMsg =
                stockTaskMapper.stockActionExecuteMsgEntityToStockActionExecuteMsg(stockActionExecuteMsgEntity);
        if (!StringUtils.isEmpty(stockActionExecuteMsgEntity.getActionStr())) {
            stockActionExecuteMsg.setAction(JsonUtils.getObjectFromStr(stockActionExecuteMsgEntity.getActionStr(), JSONObject.class));
        }
        if (!StringUtils.isEmpty(stockActionExecuteMsgEntity.getActionContextStr())) {
            stockActionExecuteMsg.setContext(JsonUtils.getObjectFromStr(stockActionExecuteMsgEntity.getActionContextStr(), StockActionContext.class));
        }
        return stockActionExecuteMsg;
    }
}
