package com.yanbin.stock.stocktaskservice.dao.data;

import com.yanbin.stock.stocktaskservice.entity.data.StockToIndustryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/27 上午8:17
 */
public interface StockToIndustryDao extends JpaRepository<StockToIndustryEntity, Integer>,
        JpaSpecificationExecutor<StockToIndustryEntity> {

}
