package com.yanbin.stock.stocktaskservice.dao.data;

import com.yanbin.stock.stocktaskservice.entity.data.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:05
 */
public interface StockDao extends JpaRepository<StockEntity, Integer> {
    List<StockEntity> findAllByCodeIn(List<String> codes);
}
