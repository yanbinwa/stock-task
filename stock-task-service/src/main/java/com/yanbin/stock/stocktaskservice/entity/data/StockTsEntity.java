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
 * @date 2020/9/24 上午8:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="stock_ts")
@EntityListeners(AuditingEntityListener.class)
public class StockTsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "time", nullable = false)
    private Timestamp time;

    @Column(name = "data")
    private String data;

    @Column(name = "update_time")
    @LastModifiedDate
    private Timestamp updateTime;
}
