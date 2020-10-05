package com.yanbin.stock.stocktaskservice.dao;

import com.yanbin.stock.stocktaskservice.entity.StockJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:04
 */
public interface StockJobDao extends JpaRepository<StockJobEntity, Integer> {
    StockJobEntity findOneById(Integer id);
    List<StockJobEntity> findAllByAppId(String appId);
}
