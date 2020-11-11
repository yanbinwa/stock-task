package com.yanbin.stock.stocktaskservice.mapper;

import com.yanbin.stock.stocktaskservice.entity.StockActionExecuteMsgEntity;
import com.yanbin.stock.stocktaskservice.entity.StockJobEntity;
import com.yanbin.stock.stocktaskservice.entity.config.UserConfigEntity;
import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;
import com.yanbin.stock.stocktaskutils.pojo.config.UserConfig;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:23
 */
@Mapper(componentModel = "spring")
@DecoratedWith(StockTaskMapperDecorator.class)
public interface StockTaskMapper {
    StockJobEntity stockJobToStockJobEntity(StockJob stockJob);
    StockJob stockJobEntityToStockJob(StockJobEntity stockJobEntity);

    StockActionExecuteMsgEntity stockActionExecuteMsgToStockActionExecuteMsgEntity(StockActionExecuteMsg stockActionExecuteMsg);
    StockActionExecuteMsg stockActionExecuteMsgEntityToStockActionExecuteMsg(StockActionExecuteMsgEntity stockActionExecuteMsgEntity);

    UserConfigEntity userConfigToUserConfigEntity(UserConfig userConfig);
    UserConfig userConfigEntityToUserConfig(UserConfigEntity userConfigEntity);
}
