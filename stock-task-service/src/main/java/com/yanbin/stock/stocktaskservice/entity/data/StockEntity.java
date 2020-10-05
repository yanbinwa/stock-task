package com.yanbin.stock.stocktaskservice.entity.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="stock")
@EntityListeners(AuditingEntityListener.class)
public class StockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url")
    private String url;

    // 现价
    @Column(name = "price")
    private Double price;

    // 涨幅比例
    @Column(name = "growthRate")
    private Double growthRate;

    // 涨幅
    @Column(name = "growth")
    private Double growth;

    // 换手率
    @Column(name = "exchange")
    private Double exchange;

    // 量比
    @Column(name = "volumeRate")
    private Double volumeRate;

    // 振幅
    @Column(name = "amplitude")
    private Double amplitude;

    // 成交额
    @Column(name = "turnover")
    private Double turnover;

    // 流通市值
    @Column(name = "market")
    private Double market;

    // 市盈率
    @Column(name = "markWin")
    private Double markWin;

    @Column(name = "update_time")
    @LastModifiedDate
    private Timestamp updateTime;
}
