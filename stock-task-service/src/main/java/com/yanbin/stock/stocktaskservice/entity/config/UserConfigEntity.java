package com.yanbin.stock.stocktaskservice.entity.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/11/11 上午7:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="user_config")
@EntityListeners(AuditingEntityListener.class)
public class UserConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "regression_config_str")
    private String regressionConfigStr;

    @Column(name = "query_config_str")
    private String queryConfigStr;

    public void update(UserConfigEntity userConfigEntity) {
        if (!StringUtils.isEmpty(userConfigEntity.getRegressionConfigStr())) {
            this.regressionConfigStr = userConfigEntity.getRegressionConfigStr();
        }
        if (!StringUtils.isEmpty(userConfigEntity.getQueryConfigStr())) {
            this.queryConfigStr = userConfigEntity.getQueryConfigStr();
        }
    }
}
