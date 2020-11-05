package com.yanbin.stock.stocktaskservice.service;

import com.emotibot.gemini.geminiutils.pojo.http.HttpFileStream;
import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.data.Stock;
import com.yanbin.stock.stocktaskutils.pojo.request.StockIndustryRequest;
import com.yanbin.stock.stocktaskutils.pojo.request.StockTestRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/3 下午5:56
 *
 * 输入一个请求，返回一个excel
 */
public interface StockTestService {
    HttpFileStream regressionTest(StockTestRequest stockTestRequest) throws StockTaskException, FileNotFoundException;
    List<Stock> wenCaiTest(String query);
    HttpFileStream stockIndustryQuery(StockIndustryRequest stockIndustryRequest) throws FileNotFoundException;
    List<String> wenCaiSuggest(String query);
}
