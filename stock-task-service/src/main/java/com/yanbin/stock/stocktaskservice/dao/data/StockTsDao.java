package com.yanbin.stock.stocktaskservice.dao.data;

import com.yanbin.stock.stocktaskservice.entity.data.StockTsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:07
 */
public interface StockTsDao extends JpaRepository<StockTsEntity, Integer> {
    StockTsEntity findOneByCodeAndTime(String code, Timestamp timestamp);
    List<StockTsEntity> findAllByCode(String code);
}
