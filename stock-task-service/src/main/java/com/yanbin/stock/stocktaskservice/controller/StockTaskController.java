package com.yanbin.stock.stocktaskservice.controller;

import com.emotibot.gemini.geminiutils.pojo.http.HttpFileStream;
import com.emotibot.gemini.geminiutils.utils.HttpFileHelper;
import com.yanbin.stock.stocktaskservice.service.StockJobManagerService;
import com.yanbin.stock.stocktaskservice.service.StockTestService;
import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;
import com.yanbin.stock.stocktaskutils.pojo.config.UserConfig;
import com.yanbin.stock.stocktaskutils.pojo.data.Stock;
import com.yanbin.stock.stocktaskutils.pojo.request.StockIndustryRequest;
import com.yanbin.stock.stocktaskutils.pojo.request.StockTestRequest;
import com.yanbin.stock.stocktaskutils.pojo.request.StockWenCaiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:53
 */
@RestController
@RequestMapping("/yanbin/stock")
public class StockTaskController {

    @Autowired
    private StockJobManagerService stockJobManagerService;

    @Autowired
    private StockTestService stockTestService;

    @Autowired
    private HttpFileHelper httpFileHelper;

    @PostMapping("/job")
    public StockJob addStockJob(@RequestHeader("appid") String appid, @RequestBody StockJob stockJob) {
        return stockJobManagerService.addStockJob(appid, stockJob);
    }

    @GetMapping("/job/{id}")
    public StockJob getStockJob(@RequestHeader("appid") String appid, @PathVariable("id") Integer id) {
        return stockJobManagerService.getStockJob(appid, id);
    }

    @PatchMapping("/job/{id}")
    public StockJob updateStockJob(@RequestHeader("appid") String appid, @PathVariable("id") Integer id,
                                   @RequestBody StockJob stockJob) throws StockTaskException {
        return stockJobManagerService.updateStockJob(appid, id, stockJob);
    }

    @PatchMapping("/job/run/{id}")
    public void runStockJob(@RequestHeader("appid") String appid, @PathVariable("id") Integer id) throws StockTaskException {
        stockJobManagerService.runStockJob(appid, id);
    }

    @CrossOrigin
    @PostMapping("/regressionTest")
    public void regressionTest(@RequestBody StockTestRequest stockTestRequest,
                               HttpServletResponse httpServletResponse) throws StockTaskException, IOException {
        HttpFileStream httpFileStream = stockTestService.regressionTest(stockTestRequest);
        if (httpFileStream == null) {
            return;
        }
        httpFileHelper.addFileToResponse(httpFileStream, httpServletResponse);
    }

    @CrossOrigin
    @PostMapping("/config")
    public void updateUserConfig(@RequestHeader("userId") Long userId, @RequestBody UserConfig userConfig) {
        stockTestService.updateUserConfig(userId, userConfig);
    }

    @CrossOrigin
    @GetMapping("/config")
    public UserConfig getUserConfig(@RequestHeader("userId") Long userId) {
        return stockTestService.getUserConfig(userId);
    }

    @PostMapping("/wenCai")
    public List<Stock> wenCai(@RequestBody StockWenCaiRequest request) {
        return stockTestService.wenCaiTest(request.getQuery());
    }

    @CrossOrigin
    @PostMapping("/stockQuery")
    public void stockIndustryQuery(@RequestBody StockIndustryRequest stockIndustryRequest,
                                   HttpServletResponse httpServletResponse) throws IOException {
        HttpFileStream httpFileStream = stockTestService.stockIndustryQuery(stockIndustryRequest);
        if (httpFileStream == null) {
            return;
        }
        httpFileHelper.addFileToResponse(httpFileStream, httpServletResponse);
    }

    @CrossOrigin
    @GetMapping("/wenCai/suggest")
    public List<String> wenCaiSuggest(@RequestParam String query) {
        return stockTestService.wenCaiSuggest(query);
    }

}
