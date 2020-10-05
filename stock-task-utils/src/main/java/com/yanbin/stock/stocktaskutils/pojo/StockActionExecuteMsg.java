package com.yanbin.stock.stocktaskutils.pojo;

import com.alibaba.fastjson.JSONObject;
import com.emotibot.gemini.geminiutils.constants.task.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:14
 *
 * 存放
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockActionExecuteMsg {
    private Integer id;
    private String appId;
    private Integer jobId;
    // 一次job执行对应的ID，如果该任务创建出子任务，也对应相同的ID
    private String taskId;
    private JSONObject action;
    private StockActionContext context;
    private TaskStatus status;
}
