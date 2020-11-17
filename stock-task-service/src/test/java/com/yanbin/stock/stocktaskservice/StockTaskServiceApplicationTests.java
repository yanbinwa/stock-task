package com.yanbin.stock.stocktaskservice;

import com.yanbin.stock.stocktaskservice.utils.StockDataHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StockTaskServiceApplicationTests {

    @Autowired
    StockDataHelper stockDataHelper;

    @Test
    void contextLoads() {

    }

}
