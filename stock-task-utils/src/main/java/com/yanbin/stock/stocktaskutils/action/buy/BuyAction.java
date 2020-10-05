package com.yanbin.stock.stocktaskutils.action.buy;

import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/29 上午10:41
 *
 *
 * 有两种方式，一种是直接读取当前股票的数据，通过stock表获取，另一个是读取历史分时数据，通过stockTs来读取，这两种同时实现
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class BuyAction extends StockAction {

    // 需要买入的code
    private String code;

    public BuyAction() {
        super();
        this.actionType = StockActionType.BUY;
    }
}
