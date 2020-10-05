package com.yanbin.stock.stocktaskutils.action.sale;

import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/29 下午11:51
 */
@Data
@AllArgsConstructor
public class SaleAction extends StockAction {

    // 需要卖出的code
    private String code;
    // 买入时的价格
    private Double price;

    public SaleAction() {
        super();
        this.actionType = StockActionType.SALE;
    }
}
