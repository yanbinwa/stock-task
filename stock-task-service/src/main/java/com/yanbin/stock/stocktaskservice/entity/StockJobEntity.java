package com.yanbin.stock.stocktaskservice.entity;

import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="stock_job")
@EntityListeners(AuditingEntityListener.class)
public class StockJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "appid", nullable = false)
    private String appId;

    @Column(name = "name")
    private String name;

    @Column(name = "cron")
    private String cron;

    @Column(name = "action_type")
    private StockActionType actionType;

    @Column(name = "action_str")
    private String actionStr;

    @Column(name = "enable")
    private Boolean enable;

    @Column(name = "update_time")
    @LastModifiedDate
    private Timestamp updateTime;

    public void update(StockJobEntity stockJobEntity) {
        if (StringUtils.isEmpty(stockJobEntity.getCron())) {
            this.cron = stockJobEntity.getCron();
        }
        if (stockJobEntity.getActionType() != null) {
            this.actionType = stockJobEntity.getActionType();
        }
        if (StringUtils.isEmpty(stockJobEntity.getActionStr())) {
            this.actionStr = stockJobEntity.getActionStr();
        }
        if (stockJobEntity.getEnable() != null) {
            this.enable = stockJobEntity.getEnable();
        }
    }
}
