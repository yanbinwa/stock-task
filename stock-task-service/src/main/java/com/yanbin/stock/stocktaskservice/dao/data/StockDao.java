package com.yanbin.stock.stocktaskservice.dao.data;

import com.yanbin.stock.stocktaskservice.entity.data.StockEntity;
import com.yanbin.stock.stocktaskservice.entity.data.StockTsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:05
 */
public interface StockDao extends JpaRepository<StockEntity, Integer> {
}
