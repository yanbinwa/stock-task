package com.yanbin.stock.stocktaskservice.dao;

import com.yanbin.stock.stocktaskservice.entity.StockActionExecuteMsgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/28 下午10:20
 */
public interface StockActionExecuteMsgDao extends JpaRepository<StockActionExecuteMsgEntity, Integer> {
    StockActionExecuteMsgEntity findOneById(Integer id);
    @Transactional
    void deleteById(Integer id);
}
