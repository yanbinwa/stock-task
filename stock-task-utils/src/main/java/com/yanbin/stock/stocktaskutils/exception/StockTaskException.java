package com.yanbin.stock.stocktaskutils.exception;

import com.emotibot.gemini.geminiutils.exception.GeminiException;
import com.yanbin.stock.stocktaskutils.constants.StockTaskResponseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:52
 */
@Data
@AllArgsConstructor
public class StockTaskException extends GeminiException {
    public StockTaskException(StockTaskResponseInfo stockTaskResponseInfo) {
        this.code = stockTaskResponseInfo.getCode();
        this.message = stockTaskResponseInfo.getMessage();
    }

    public StockTaskException(StockTaskResponseInfo stockTaskResponseInfo, String message) {
        this.code = stockTaskResponseInfo.getCode();
        this.message = message;
    }
}
