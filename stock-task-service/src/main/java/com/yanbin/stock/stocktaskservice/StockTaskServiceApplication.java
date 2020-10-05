package com.yanbin.stock.stocktaskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"com.yanbin.stock", "com.emotibot.gemini"})
public class StockTaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTaskServiceApplication.class, args);
    }

}
