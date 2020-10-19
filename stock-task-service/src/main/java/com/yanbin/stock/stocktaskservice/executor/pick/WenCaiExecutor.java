package com.yanbin.stock.stocktaskservice.executor.pick;

import com.yanbin.stock.stocktaskservice.annotation.StockExecutor;
import com.yanbin.stock.stocktaskservice.executor.BaseStockExecutor;
import com.yanbin.stock.stocktaskservice.utils.StockDataHelper;
import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.action.pick.WenCaiAction;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import com.yanbin.stock.stocktaskutils.pojo.StockActionContext;
import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;
import com.yanbin.stock.stocktaskutils.pojo.data.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:28
 */
@Component
@StockExecutor(type = StockActionType.WEN_CAI)
public class WenCaiExecutor extends BaseStockExecutor {

    private static DateFormat dateFormat = new SimpleDateFormat("MM月dd日");

    @Autowired
    StockDataHelper stockDataHelper;

    /**
     * 先获取time列表，如果是多个，需要对列表取交集，将最终的股票筛选出来
     *
     * 先通过content来获取日期，如果日期为null，默认为当天数据，不写
     *
     * 如果有多个time，需要逐一拼接成query，分别获取stock列表，再求和
     *
     * @param stockAction
     * @param stockActionExecuteMsg
     */
    @Override
    public Boolean executeAction(StockAction stockAction, StockActionExecuteMsg stockActionExecuteMsg) {
        WenCaiAction wenCaiAction = (WenCaiAction) stockAction;
        String queryTemplate = wenCaiAction.getTemplate();
        if (StringUtils.isEmpty(queryTemplate)) {
            return false;
        }
        StockActionContext stockActionContext = stockActionExecuteMsg.getContext();
        String dateStr = getDateStr(stockActionContext);
        List<Stock> stockCodes = chooseStock(dateStr, wenCaiAction, stockActionContext);
        if (CollectionUtils.isEmpty(stockCodes)) {
            return true;
        }
        // 将不同的stock生成不同buyExecutor action，计算触发的时间条件
        List<StockActionExecuteMsg> actionExecuteMsgs = stockCodes.stream().map(t -> buildBuyAction(t, wenCaiAction, stockActionExecuteMsg))
                .collect(Collectors.toList());
        actionExecuteMsgs.forEach(t -> stockJobAdminService.scheduleStockAction(t));
        return true;
    }

    private String getDateStr(StockActionContext stockActionContext) {
        StockActionContext.ContextElement contextElement = stockActionContext.getContextElementMap().get(StockActionType.WEN_CAI);
        if (contextElement == null || contextElement.getDate() == null) {
            return null;
        }
        return dateFormat.format(contextElement.getDate());
    }

    // 需要将筛选的条件写入到StockActionContext中
    private List<Stock> chooseStock(String dataStr, WenCaiAction wenCaiAction, StockActionContext stockActionContext) {
        List<String> queries = new ArrayList<>();
        for (String time : wenCaiAction.getTimes()) {
            queries.add(String.format(wenCaiAction.getTemplate(), StringUtils.isEmpty(dataStr) ? time : dataStr + time));
        }
        return stockDataHelper.fetchStockByWenCai(queries);
    }

    /**
     * 1. 确定执行购买的时间节点，需要考虑节假日的
     * 2.
     *
     * @param stock
     * @param wenCaiAction
     * @param stockActionExecuteMsg
     * @return
     */
    private StockActionExecuteMsg buildBuyAction(Stock stock, WenCaiAction wenCaiAction, StockActionExecuteMsg stockActionExecuteMsg) {
        return null;
    }
}
