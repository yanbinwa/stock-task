package com.yanbin.stock.stocktaskutils.pojo;

import com.alibaba.fastjson.JSONObject;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:49
 *
 * 存放action输出结果的上下文，方便action与action之间数据的流转和共享
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockActionContext {
    // 执行的时间
    private Map<StockActionType, ContextElement> contextElementMap;

    /**
     * date指的是发生的时间，如果是回测，对应的就是历史的时间。这里的时间是由
     *
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContextElement {
        private String date;
        private JSONObject data;
    }

    public void putContextElement(StockActionType stockActionType, ContextElement contextElement) {
        if (contextElementMap == null) {
            contextElementMap = new HashMap<>();
        }
        contextElementMap.put(stockActionType, contextElement);
    }
}
