package com.yanbin.stock.stocktaskutils.pojo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/11/11 上午7:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConfig {
    private Long id;
    private Long userId;
    private RegressionConfig regressionConfig;
    private QueryConfig queryConfig;
}
