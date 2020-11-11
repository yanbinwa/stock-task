package com.yanbin.stock.stocktaskservice.service.impl;

import com.emotibot.gemini.geminiutils.pojo.http.HttpFileElement;
import com.emotibot.gemini.geminiutils.pojo.http.HttpFileStream;
import com.emotibot.gemini.geminiutils.utils.FileUtils;
import com.emotibot.gemini.geminiutils.utils.JsonUtils;
import com.emotibot.gemini.geminiutils.utils.RedisHelper;
import com.emotibot.gemini.geminiutils.utils.UuidUtils;
import com.yanbin.stock.stocktaskservice.dao.config.UserConfigDao;
import com.yanbin.stock.stocktaskservice.entity.config.UserConfigEntity;
import com.yanbin.stock.stocktaskservice.mapper.StockTaskMapper;
import com.yanbin.stock.stocktaskservice.service.StockTestService;
import com.yanbin.stock.stocktaskservice.utils.NumUtils;
import com.yanbin.stock.stocktaskservice.utils.StockDataHelper;
import com.yanbin.stock.stocktaskservice.utils.StockTimeHelper;
import com.yanbin.stock.stocktaskutils.constants.StockTaskConstants;
import com.yanbin.stock.stocktaskutils.constants.StockTaskResponseInfo;
import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.config.UserConfig;
import com.yanbin.stock.stocktaskutils.pojo.data.Industry;
import com.yanbin.stock.stocktaskutils.pojo.data.Stock;
import com.yanbin.stock.stocktaskutils.pojo.data.StockIndustry;
import com.yanbin.stock.stocktaskutils.pojo.request.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/3 下午6:11
 */
@Service
public class StockTestServiceImpl implements StockTestService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy年MM月dd日");
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy年MM月dd日 HH:mm:ss");
    private static final DateTimeFormatter queryTimeFormatter = DateTimeFormat.forPattern("HH点mm分");
    private static final List<String> TEST_HEADER = Arrays.asList("股票代码", "股票名称", "买入时间", "买入价格", "卖出时间", "卖出价格", "收益");
    private static final String STOCK_TEST_DIR = "/stockTest";
    private static final Integer DEFAULT_INDUSTRY_ORDER = 10000;
    private static final List<String> STOCK_QUERY_HEADER = Arrays.asList("股票代码", "股票名称", "价格", "涨幅", "行业", "行业排名", "行业涨幅", "行业资金流入");
    private static final Integer WENCAI_SUGGEST_MAX_INPUT_SIZE = 10;


    @Autowired
    StockDataHelper stockDataHelper;

    @Autowired
    StockTimeHelper stockTimeHelper;

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    UserConfigDao userConfigDao;

    @Autowired
    StockTaskMapper stockTaskMapper;


    @PostConstruct
    private void init() {
        File file = new File(buildStockTaskTmpDir());
        if (file.exists()) {
            return;
        }
        file.mkdir();
    }

    /**
     * 1. 按照startTime和endTime来分时间，按照每一天执行，需要判断节假日和周末
     * 2. 每一天通过问财返回一批股票列表，每一支股票返回一个testResult结果
     * 3. 每一支股票，通过buy和sale rule，先确定买入和卖出的date（需要跳过节假日和周末），通过买入和卖出的时间，获取对应的价格，再计算收益
     *
     * @param stockTestRequest
     * @return
     */
    @Override
    public HttpFileStream regressionTest(StockTestRequest stockTestRequest) throws StockTaskException, FileNotFoundException {
        DateTime startDateTime = new DateTime(stockTestRequest.getStartTime());
        DateTime endDateTime = new DateTime(stockTestRequest.getEndTime());
        if (startDateTime == null || endDateTime == null) {
            throw new StockTaskException(StockTaskResponseInfo.STOCK_TASK_JOB_NOT_EXIST);
        }
        Map<DateTime, List<StockTestResult>> stockTestRequestMap = new HashMap<>();
        List<DateTime> dateTimes = new ArrayList<>();
        // 获取
        for (int i = 0; i <= endDateTime.getDayOfYear() - startDateTime.getDayOfYear(); i ++) {
            DateTime dateTime = startDateTime.plusDays(i);
            if (!stockTimeHelper.isOpeningDate(dateTime)) {
                continue;
            }
            dateTimes.add(dateTime);
            List<StockTestResult> stockTestResults = calculateStockTestResult(dateTime, stockTestRequest);
            if (!CollectionUtils.isEmpty(stockTestResults)) {
                stockTestRequestMap.put(dateTime, stockTestResults);
            }
        }

        // 输出成excel，返回InputStream 或其他
        List<String> sheetNames = dateTimes.stream().map(t -> t.toString(dateFormatter)).collect(Collectors.toList());
        Map<String, List<List<String>>> sheetNameToContentMap = new HashMap<>();
        for (DateTime dateTime : dateTimes) {
            List<StockTestResult> stockTestResults = stockTestRequestMap.get(dateTime);
            List<List<String>> content = new ArrayList<>();
            content.add(TEST_HEADER);
            if (CollectionUtils.isEmpty(stockTestResults)) {
                // 如果stockTestRequestMap结果为空，需要确保文件是正常的
                content.add(Arrays.asList("合计", "0"));
                sheetNameToContentMap.put(dateTime.toString(dateFormatter), content);
                continue;
            }
            content.addAll(stockTestResults.stream().sorted(Comparator.comparing(StockTestResult::getIncome))
                    .map(t -> Arrays.asList(t.getCode(), t.getName(), t.getBuyTime(), String.valueOf(t.getBuyPrice()), t.getSaleTime(),
                            String.valueOf(t.getSalePrice()), NumUtils.buildPercentageNum(t.getIncome())))
                    .collect(Collectors.toList()));
            content.add(Arrays.asList("合计", NumUtils.buildPercentageNum(stockTestResults.stream().mapToDouble(StockTestResult::getIncome).sum() / stockTestResults.size())));
            sheetNameToContentMap.put(dateTime.toString(dateFormatter), content);
        }
        String filePath = buildStockTaskTmpFile();
        FileUtils.writeLogForXls(filePath, sheetNameToContentMap, sheetNames);
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        file.delete();
        return HttpFileStream.builder().inputStream(inputStream).httpFileElement(HttpFileElement.builder()
                .name(file.getName()).build()).build();
    }

    @Override
    public List<Stock> wenCaiTest(String query) {
        return stockDataHelper.fetchStockByWenCai(query);
    }

    /**
     * 1. 调用问财数据获取股票列表
     * 2. 调用行业信息获取行业列表
     * 3. 通过股票和行业映射关系，将topN的行业标注在股票上，如果一个股票对应多个行业，写多份
     * 4.
     *
     * @param stockIndustryRequest
     * @return
     */
    @Override
    public HttpFileStream stockIndustryQuery(StockIndustryRequest stockIndustryRequest) throws FileNotFoundException {
        // TODO 需要判断执行time时是否比当前时间小，如果小，date是今天，如果大，date就是昨天
        DateTime dateTime = getStockQueryDate(stockIndustryRequest);
        List<Stock> stocks = queryStock(dateTime, stockIndustryRequest);
        // 2. 调用行业信息获取行业列表
        List<Industry> topIndustry = stockDataHelper.fetchTopIndustry(stockIndustryRequest.getIndustrySize());
        Map<String, Industry> nameToIndustryMap = new HashMap<>();
        Map<String, Integer> nameToIndexMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(topIndustry)) {
            topIndustry.stream().forEach(t -> nameToIndustryMap.put(t.getName(), t));
            IntStream.range(0, topIndustry.size()).forEach(t -> nameToIndexMap.put(topIndustry.get(t).getName(), t + 1));
        }
        // 3. 获取全部的股票和行业映射信息
        Map<String, List<String>> stockCodeToIndustryNameMap = stockDataHelper.getStockToIndustryMap();
        // 4. 遍历数据，按照行业排名来排序，如果没有，就写入默认排序值，再最终写入之前排除掉
        List<StockIndustry> stockIndustryList = new ArrayList<>();
        for (Stock stock : stocks) {
            StockIndustry stockIndustry = JsonUtils.copyObject(stock, StockIndustry.class);
            List<String> industryNameList = stockCodeToIndustryNameMap.get(stock.getCode());
            if (CollectionUtils.isEmpty(industryNameList)) {
                stockIndustry.setIndustryOrder(DEFAULT_INDUSTRY_ORDER);
            } else {
                Boolean ret = false;
                for (String industryName : industryNameList) {
                    Industry industry = nameToIndustryMap.get(industryName);
                    if (industry != null) {
                        stockIndustry.setIndustryOrder(nameToIndexMap.get(industryName));
                        stockIndustry.setIndustryName(industryName);
                        stockIndustry.setIndustryGrowthRate(industry.getGrowthRate());
                        stockIndustry.setIndustryNetInflow(industry.getNetInflow());
                        ret = true;
                        break;
                    }
                }
                if (!ret) {
                    stockIndustry.setIndustryOrder(DEFAULT_INDUSTRY_ORDER);
                    stockIndustry.setIndustryName(industryNameList.get(0));
                }
            }
            stockIndustryList.add(stockIndustry);
        }
        // 5. 将结果按照industry的order排序后写入到文档中
        stockIndustryList = stockIndustryList.stream().sorted(Comparator.comparing(StockIndustry::getIndustryOrder))
                .collect(Collectors.toList());
        List<List<String>> contents = new ArrayList<>();
        contents.add(STOCK_QUERY_HEADER);
        //"股票代码", "股票名称", "价格", "涨幅", "行业", "行业排名", "行业涨幅", "行业资金流入"
        stockIndustryList.forEach(t -> {
            if (t.getIndustryOrder() == DEFAULT_INDUSTRY_ORDER) {
                contents.add(Arrays.asList(t.getCode(), t.getName(), String.valueOf(t.getPrice()), String.valueOf(t.getGrowthRate()),
                        StringUtils.isEmpty(t.getIndustryName()) ? "" : t.getIndustryName()));
            } else {
                contents.add(Arrays.asList(t.getCode(), t.getName(), String.valueOf(t.getPrice()), String.valueOf(t.getGrowthRate()),
                        t.getIndustryName(), String.valueOf(t.getIndustryOrder()), String.valueOf(t.getIndustryGrowthRate()), String.valueOf(t.getIndustryNetInflow())));
            }
        });
        String filePath = buildStockTaskTmpFile();
        FileUtils.writeLogForXls(filePath, contents);
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        file.delete();
        return HttpFileStream.builder().inputStream(inputStream).httpFileElement(HttpFileElement.builder()
                .name(file.getName()).build()).build();
    }

    @Override
    public List<String> wenCaiSuggest(String query) {
        if (StringUtils.isEmpty(query) || query.length() > WENCAI_SUGGEST_MAX_INPUT_SIZE) {
            return null;
        }
        return stockDataHelper.wenCaiSuggest(query);
    }

    @Override
    public void addWenCaiToken(WeiCaiTokenRequest weiCaiTokenRequest) {
        stockDataHelper.addWenCaiToken(weiCaiTokenRequest);
    }

    /**
     * 判读用户配置是否存在，如果存在，在原有基础上修改，如果不存在，直接新增
     *
     * @param userId
     * @param userConfig
     */
    @Override
    public void updateUserConfig(Long userId, UserConfig userConfig) {
        UserConfigEntity userConfigEntity = userConfigDao.findOneByUserId(userId);
        if (userConfigEntity == null) {
            userConfigEntity = UserConfigEntity.builder().userId(userId).build();
        }
        UserConfigEntity newUserConfigEntity = stockTaskMapper.userConfigToUserConfigEntity(userConfig);
        userConfigEntity.update(newUserConfigEntity);
        userConfigDao.save(userConfigEntity);
    }

    @Override
    public UserConfig getUserConfig(Long userId) {
        UserConfigEntity userConfigEntity = userConfigDao.findOneByUserId(userId);
        if (userConfigEntity == null) {
            return null;
        }
        return stockTaskMapper.userConfigEntityToUserConfig(userConfigEntity);
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
        List<Stock> chooseStocks = queryStock(dateTime, stockTestRequest);
        if (CollectionUtils.isEmpty(chooseStocks)) {
            return null;
        }
        // 每只股票进行计算
        return chooseStocks.stream().map(t -> calculateStockTestResult(t, dateTime, stockTestRequest))
                .filter(t -> t != null).collect(Collectors.toList());
    }

    /**
     * 获取特定时间的股票收益
     *
     * @param dateTime
     * @param stockQueryRequest
     * @return
     */
    private List<Stock> queryStock(DateTime dateTime, StockQueryRequest stockQueryRequest) {
        if (dateTime == null) {
            // 按照当天的时间
            dateTime = new DateTime();
        }
        List<String> queries = new ArrayList<>();
        String query = stockQueryRequest.getQueryTemplate();
        query = query.replace(StockTaskConstants.DATE_PLACE_HOLDER, stockTimeHelper.buildDayStr(dateTime));
        query = query.replace(StockTaskConstants.LAST_DATE_PLACE_HOLDER, stockTimeHelper.buildDayStr(stockTimeHelper.buildLastDealDay(dateTime, 1)));
        queries.add(query);
        return stockDataHelper.fetchStockByWenCai(queries);
    }

    private StockTestResult calculateStockTestResult(Stock stock, DateTime dateTime, StockTestRequest stockTestRequest) {
        StockTestResult stockTestResult = new StockTestResult();
        stockTestResult.setDate(dateTime.toString(dateFormatter));
        stockTestResult.setCode(stock.getCode());
        stockTestResult.setName(stock.getName());
        DateTime buyDateTime = stockTimeHelper.buildDateTime(dateTime, stockTestRequest.getBuyRule());
        Stock buyStock = stockDataHelper.fetchStockByTime(stock.getCode(), buyDateTime);
        DateTime saleDateTime = stockTimeHelper.buildDateTime(dateTime, stockTestRequest.getSaleRule());
        Stock saleStock = stockDataHelper.fetchStockByTime(stock.getCode(), saleDateTime);
        if (buyStock == null || saleStock == null) {
            return null;
        }
        stockTestResult.setBuyTime(buyDateTime.toString(timeFormatter));
        stockTestResult.setBuyPrice(buyStock.getPrice());
        stockTestResult.setSaleTime(saleDateTime.toString(timeFormatter));
        stockTestResult.setSalePrice(saleStock.getPrice());
        stockTestResult.setIncome((stockTestResult.getSalePrice() - stockTestResult.getBuyPrice()) / stockTestResult.getBuyPrice());
        return stockTestResult;
    }

    /**
     * 比较给定的time和当前time之间的关系
     *
     * 需要判断当前时间与股市开盘时间做比较
     *
     * @param stockQueryRequest
     * @return
     */
    private DateTime getStockQueryDate(StockQueryRequest stockQueryRequest) {
        DateTime currentDataTime = new DateTime();
        DateTime openTime = currentDataTime.withYear(currentDataTime.getYear()).withDayOfYear(currentDataTime.getDayOfYear())
                .withHourOfDay(9).withMinuteOfHour(30);
        if (currentDataTime.isBefore(openTime)) {
            currentDataTime = currentDataTime.withDayOfYear(currentDataTime.getDayOfYear() - 1);
        }
        return currentDataTime;
    }

    private String buildStockTaskTmpFile() {
        return buildStockTaskTmpDir() + "/" + UuidUtils.buildUuid() + ".xlsx";
    }

    private String buildStockTaskTmpDir() {
        return FileUtils.getTmpDir() + STOCK_TEST_DIR;
    }
}
