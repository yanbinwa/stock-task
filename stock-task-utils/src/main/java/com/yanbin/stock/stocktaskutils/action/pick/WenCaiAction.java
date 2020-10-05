package com.yanbin.stock.stocktaskutils.action.pick;

import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:19
 */
@Data
@AllArgsConstructor
public class WenCaiAction extends StockAction {

    // 问财的输入模板，例如："%s时，大单净比大于8.8，量比大于3.8，剔除ST，无立案调查，创业板，上一个交易日收盘价小于5日线*1.18，%s时换手率大于0.8%，昨日涨跌幅小于15%"
    private String template;
    // 9点32分，9点33分，9点34分
    private List<String> times;
    private List<String> includeStocks;
    private List<String> excludeStocks;

    public WenCaiAction() {
        super();
        this.actionType = StockActionType.WEN_CAI;
    }
}
