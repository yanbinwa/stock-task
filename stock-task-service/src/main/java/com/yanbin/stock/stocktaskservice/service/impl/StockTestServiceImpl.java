package com.yanbin.stock.stocktaskservice.service.impl;

import com.emotibot.gemini.geminiutils.utils.FileUtils;
import com.emotibot.gemini.geminiutils.utils.UuidUtils;
import com.yanbin.stock.stocktaskservice.service.StockTestService;
import com.yanbin.stock.stocktaskservice.utils.StockDataHelper;
import com.yanbin.stock.stocktaskservice.utils.StockTimeHelper;
import com.yanbin.stock.stocktaskutils.constants.StockTaskConstants;
import com.yanbin.stock.stocktaskutils.constants.StockTaskResponseInfo;
import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.data.Stock;
import com.yanbin.stock.stocktaskutils.pojo.request.StockTestRequest;
import com.yanbin.stock.stocktaskutils.pojo.request.StockTestResult;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/3 下午6:11
 */
@Service
public class StockTestServiceImpl implements StockTestService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy年MM月dd日");
    private static final List<String> TEST_HEADER = Arrays.asList("股票代码", "买入时间", "买入价格", "卖出时间", "卖出价格");
    private static final String STOCK_TEST_DIR = "/stockTest";

    @Autowired
    StockDataHelper stockDataHelper;

    @Autowired
    StockTimeHelper stockTimeHelper;

    /**
     * 1. 按照startTime和endTime来分时间，按照每一天执行，需要判断节假日和周末
     * 2. 每一天通过问财返回一批股票列表，每一支股票返回一个testResult结果
     * 3. 每一支股票，通过buy和sale rule，先确定买入和卖出的date（需要跳过节假日和周末），通过买入和卖出的时间，获取对应的价格，再计算收益
     *
     * @param stockTestRequest
     * @return
     */
    @Override
    public void regressionTest(StockTestRequest stockTestRequest) throws StockTaskException {
        DateTime startDateTime = dateFormatter.parseDateTime(stockTestRequest.getStartTime());
        DateTime endDateTime = dateFormatter.parseDateTime(stockTestRequest.getEndTime());
        if (startDateTime == null || endDateTime == null) {
            throw new StockTaskException(StockTaskResponseInfo.STOCK_TASK_JOB_NOT_EXIST);
        }
        Map<DateTime, List<StockTestResult>> stockTestRequestMap = new HashMap<>();
        // 获取
        for (int i = 0; i <= endDateTime.getDayOfYear() - startDateTime.getDayOfYear(); i ++) {
            DateTime dateTime = startDateTime.plusDays(i);
            if (!stockTimeHelper.isOpeningDate(dateTime)) {
                continue;
            }
            List<StockTestResult> stockTestResults = calculateStockTestResult(dateTime, stockTestRequest);
            if (!CollectionUtils.isEmpty(stockTestResults)) {
                stockTestRequestMap.put(dateTime, stockTestResults);
            }
        }
        // 输出成excel，返回InputStream 或其他
        List<DateTime> dateTimes = stockTestRequestMap.keySet().stream().sorted().collect(Collectors.toList());
        List<String> sheetNames = dateTimes.stream().map(t -> t.toString(dateFormatter)).collect(Collectors.toList());
        Map<String, List<List<String>>> sheetNameToContentMap = new HashMap<>();
        for (DateTime dateTime : dateTimes) {
            List<StockTestResult> stockTestResults = stockTestRequestMap.get(dateTime);
            List<List<String>> content = stockTestResults.stream().sorted(Comparator.comparing(StockTestResult::getIncome))
                    .map(t -> Arrays.asList(t.getCode(), t.getBuyTime(), String.valueOf(t.getBuyPrice()), t.getSaleTime(), String.valueOf(t.getSalePrice())))
                    .collect(Collectors.toList());
            content.add(Arrays.asList(String.valueOf(stockTestResults.stream().mapToDouble(StockTestResult::getIncome).sum() / stockTestResults.size())));
            sheetNameToContentMap.put(dateTime.toString(dateFormatter), content);
        }
        String filePath = buildStockTaskTmpFile();
        FileUtils.writeLogForXls(filePath, sheetNameToContentMap, sheetNames);
    }

    /**
     * 1. 获取stock列表
     * 2. 计算出买入的时间点
     * 3. 计算出卖出的时间点
     * 4. 获取买入的价格
     * 5. 获取卖出的价格
     *
     * @param dateTime    基准时间
     * @param stockTestRequest
     * @return
     */
    private List<StockTestResult> calculateStockTestResult(DateTime dateTime, StockTestRequest stockTestRequest) {
        List<String> chooseStockCodes = queryStock(dateTime, stockTestRequest);
        if (CollectionUtils.isEmpty(chooseStockCodes)) {
            return null;
        }
        // 每只股票进行计算
        return chooseStockCodes.stream().map(t -> calculateStockTestResult(t, dateTime, stockTestRequest))
                .filter(t -> t != null).collect(Collectors.toList());
    }

    private List<String> queryStock(DateTime dateTime, StockTestRequest stockTestRequest) {
        List<String> queries = new ArrayList<>();
        for (String time : stockTestRequest.getQueryTimes()) {
            queries.add(stockTestRequest.getQueryTemplate().replace(StockTaskConstants.QUERY_PLACE_HOLDER,
                    dateTime == null ? time : dateTime.toString(dateFormatter) + time));
        }
        return stockDataHelper.fetchStockByWenCai(queries);
    }

    /**
     *
     * @param code
     * @param stockTestRequest
     * @return
     */
    private StockTestResult calculateStockTestResult(String code, DateTime dateTime, StockTestRequest stockTestRequest) {
        StockTestResult stockTestResult = new StockTestResult();
        stockTestResult.setDate(dateTime.toString(dateFormatter));
        stockTestResult.setCode(code);
        DateTime buyDateTime = stockTimeHelper.buildDateTime(dateTime, stockTestRequest.getBuyRule());
        Stock buyStock = stockDataHelper.fetchStockByTime(code, buyDateTime);
        DateTime saleDateTime = stockTimeHelper.buildDateTime(dateTime, stockTestRequest.getSaleRule());
        Stock saleStock = stockDataHelper.fetchStockByTime(code, saleDateTime);
        if (buyStock == null || saleStock == null) {
            return null;
        }
        stockTestResult.setBuyTime(buyDateTime.toString(dateFormatter));
        stockTestResult.setBuyPrice(buyStock.getPrice());
        stockTestResult.setSaleTime(saleDateTime.toString(dateFormatter));
        stockTestResult.setSalePrice(saleStock.getPrice());
        stockTestResult.setIncome((stockTestResult.getSalePrice() - stockTestResult.getSalePrice()) / stockTestResult.getBuyPrice());
        return stockTestResult;
    }

    private String buildStockTaskTmpFile() {
        return FileUtils.getTmpDir() + STOCK_TEST_DIR + "/" + UuidUtils.buildUuid() + ".xlsx";
    }
}
