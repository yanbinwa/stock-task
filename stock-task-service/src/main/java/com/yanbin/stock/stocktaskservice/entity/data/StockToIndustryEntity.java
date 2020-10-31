package com.yanbin.stock.stocktaskservice.entity.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/27 上午8:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="stock_to_industry")
@EntityListeners(AuditingEntityListener.class)
public class StockToIndustryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "stock_code", nullable = false)
    private String stockCode;

    @Column(name = "industry_name", nullable = false)
    private String industryName;

}
