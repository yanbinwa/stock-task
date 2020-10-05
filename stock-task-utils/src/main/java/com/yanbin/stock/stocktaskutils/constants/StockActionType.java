package com.yanbin.stock.stocktaskutils.constants;

import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.action.buy.BuyAction;
import com.yanbin.stock.stocktaskutils.action.pick.WenCaiAction;
import com.yanbin.stock.stocktaskutils.action.sale.SaleAction;
import lombok.Getter;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/22 上午8:31
 */
@Getter
public enum StockActionType {

    WEN_CAI(WenCaiAction.class),
    BUY(BuyAction.class),
    SALE(SaleAction.class),
    ;

    private Class<? extends StockAction> clazz;

    StockActionType(Class<? extends StockAction> clazz) {
        this.clazz = clazz;
    }

    public static StockActionType buildActionType(String name) {
        for (StockActionType stockActionType : values()) {
            if (stockActionType.name().equals(name)) {
                return stockActionType;
            }
        }
        return null;
    }
}
