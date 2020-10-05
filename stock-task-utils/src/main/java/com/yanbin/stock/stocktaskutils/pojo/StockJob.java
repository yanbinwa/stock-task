package com.yanbin.stock.stocktaskutils.pojo;

import com.alibaba.fastjson.JSONObject;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:38
 *
 *  Job就是指定期执行的任务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockJob {

    private Integer id;
    private String appId;
    private String name;
    private String cron;
    private StockActionType type;
    private JSONObject stockAction;
    private Boolean enable;

}
