package com.yanbin.stock.stocktaskservice.dao.config;

import com.yanbin.stock.stocktaskservice.entity.config.UserConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/11/11 上午7:41
 */
public interface UserConfigDao extends JpaRepository<UserConfigEntity, Integer> {
    UserConfigEntity findOneByUserId(Long userId);
}
