package com.yanbin.stock.stocktaskutils.action;

import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import com.yanbin.stock.stocktaskutils.pojo.deal.DealRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/22 上午8:22
 *
 * 这里是一个任务，包括
 *
 * 1. 选股任务（调用问财接口）
 * 2. 买入任务（可以通过选股任务来触发）
 * 3. 卖出任务（可以通过买入任务触发，根据规则，计算卖出点，通过实时股票信息来）
 * 4. 回测任务
 *
 * 以上任务需要通过数据库存放（并可以通过redis来缓存），并且实时轮询redis，当满足任务的触发条件时，执行
 *
 * 有些定时任务会写入到cron job里维护，定时触发
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAction {

    protected StockActionType actionType;
    // 一般是自动生成的action
    protected Long executeTime;

    // TODO 买入规则
    protected DealRule buyRule;
    // TODO 卖出规则
    protected DealRule saleRule;
}
