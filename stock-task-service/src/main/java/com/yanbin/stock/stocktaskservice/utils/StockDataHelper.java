package com.yanbin.stock.stocktaskservice.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.gemini.geminiutils.utils.HttpHelper;
import com.emotibot.gemini.geminiutils.utils.JsonUtils;
import com.emotibot.gemini.geminiutils.utils.MinioHelper;
import com.yanbin.stock.stocktaskservice.dao.data.StockDao;
import com.yanbin.stock.stocktaskservice.dao.data.StockTsDao;
import com.yanbin.stock.stocktaskservice.entity.data.StockTsEntity;
import com.yanbin.stock.stocktaskutils.constants.StockTaskConstants;
import com.yanbin.stock.stocktaskutils.pojo.data.Stock;
import com.yanbin.stock.stocktaskutils.pojo.data.StockTs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/23 上午8:34
 *
 * 获取股票数据工具类，包括某只股票的实时数据，历史分时数据等
 *
 * 有些是可以通过spider来调用，spider提供代理和cookie等能力
 */
@Slf4j
@Component
public class StockDataHelper {

    // 问财
    private static final String WEN_CAI_URL = "http://www.iwencai.com/unifiedwap/unified-wap/v2/result/get-robot-data?source=Ths_iwencai_Xuangu&version=2.0";
    private static final String WEN_CAI_ADD_INFO = "{\"urp\":{\"scene\":1,\"company\":1,\"business\":8},\"contentType\":\"json\"}";

    // 问财股票信息KEY
    private static final String WEN_CAI_STOCK_CODE_KEY = "code";

    private static final String WEN_CAI_STOCK_NAME_KEY = "股票简称";

    private static final DateTimeFormatter STOCK_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");

    private static final DateTimeFormatter STOCK_TS_TIME_FORMATTER = DateTimeFormat.forPattern("HHmm");

    @Autowired
    HttpHelper httpHelper;

    @Autowired
    StockTsDao stockTsDao;

    @Autowired
    StockDao stockDao;

    @Autowired
    MinioHelper minioHelper;

    /**
     * 目前问财接口只需要返回code就可以了
     *
     * @param query
     * @return
     */
    public List<Stock> fetchStockByWenCai(String query) {

        Map<String, Object> formMap = new HashMap<>();
        formMap.put("question", query);
        formMap.put("page", 1);
        formMap.put("perpage", 20);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        JSONObject jsonObject = httpHelper.postForm(WEN_CAI_URL, headerMap, null, formMap, JSONObject.class);
        Integer code = jsonObject.getInteger("status_code");
        if (code != 0) {
            log.error("wen cai");
            return null;
        }
        try {
            List<Stock> stocks = new ArrayList<>();
            JSONArray dataObj = jsonObject.getJSONObject("data").getJSONArray("answer").getJSONObject(0)
                    .getJSONArray("txt").getJSONObject(0).getJSONObject("content").getJSONArray("components")
                    .getJSONObject(0).getJSONObject("data").getJSONArray("datas");
            IntStream.range(0, dataObj.size()).forEach(t ->
                    stocks.add(Stock.builder().code(dataObj.getJSONObject(t).getString(WEN_CAI_STOCK_CODE_KEY))
                            .name(dataObj.getJSONObject(t).getString(WEN_CAI_STOCK_NAME_KEY)).build()));
            return stocks;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("wen cai data");
            return null;
        }
    }

    public List<Stock> fetchStockByWenCai(List<String> queries) {
        List<Stock> chosenStock = new ArrayList<>();
        for (String query : queries) {
            List<Stock> stocks = fetchStockByWenCai(query);
            // 一旦没有选出的股票
            if (CollectionUtils.isEmpty(stocks)) {
                return null;
            } else {
                if (CollectionUtils.isEmpty(chosenStock)) {
                    chosenStock.addAll(stocks);
                } else {
                    // 需要对于chosenStock和stocks取交集，遍历chosenStock，如果没有
                    Map<String, Stock> codeToStockMap = chosenStock.stream().collect(Collectors.toMap(Stock::getName,
                            Function.identity()));
                    chosenStock = stocks.stream().filter(t -> codeToStockMap.containsKey(t.getCode()))
                            .collect(Collectors.toList());
                }
            }
            if (CollectionUtils.isEmpty(chosenStock)) {
                return null;
            }
        }
        return chosenStock;
    }

    /**
     * 1. 先获取minio的文件路径，时间是11:00
     * 2. 再获取json内容，按照时间获取结果
     *
     * @param code
     * @param dateTime
     * @return
     */
    public Stock fetchStockByTime(String code, DateTime dateTime) {
        // 日期搜索条件
        DateTime date = new DateTime(dateTime);
        date = date.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        StockTsEntity stockTsEntity = stockTsDao.findOneByCodeAndTime(code, new Timestamp(date.getMillis()));
        if (stockTsEntity == null) {
            return null;
        }
        InputStream inputStream = minioHelper.download(StockTaskConstants.SPIDER_BUCKET, stockTsEntity.getData());
        String stockTsStr;
        try {
            stockTsStr = IOUtils.toString(inputStream, String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        StockTs stockTs = JsonUtils.getObjectFromStr(stockTsStr, StockTs.class);
        StockTs.StockTsElement stockTsElement =
                stockTs.getElements().stream().filter(t -> t.getTime().equals(dateTime.toString(STOCK_TS_TIME_FORMATTER)))
                        .findFirst().orElse(null);
        if (stockTsElement == null) {
            return null;
        }
        return Stock.builder().code(code).price(stockTsElement.getPrice()).build();
    }
}
