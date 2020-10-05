package com.yanbin.stock.stocktaskservice.annotation;

import com.yanbin.stock.stocktaskutils.constants.StockActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StockExecutor {
    StockActionType type();
}
