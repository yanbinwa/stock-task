package com.yanbin.stock.stocktaskservice.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emotibot.gemini.geminiutils.utils.HttpHelper;
import com.yanbin.stock.stocktaskutils.constants.StockTaskResponseInfo;
import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.deal.DealRule;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/10/4 上午8:00
 */
@Component
public class StockTimeHelper {

    private static final String HOLIDAY_URL = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=%d&resource_id=6018";
    private static final DateTimeFormatter HOLIDAY_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("hh点mm分");
    private static final List<Integer> HOLIDAY_YEARS = Arrays.asList(2020);

    @Autowired
    HttpHelper httpHelper;

    private Set<String> holidays = new HashSet<>();

    @PostConstruct
    private void init() throws StockTaskException {
        //fetchHoliday();
    }

    /**
     * 计算第二天9点32分的时间
     *
     * 1. offset: 1
     * 2. time: 9点32分
     *
     * @param dateTime
     * @param offset
     * @param time
     * @return
     */
    public DateTime buildDateTime(DateTime dateTime, Integer offset, String time) {
        // 通过offset来获取日期
        int step = 0;
        DateTime ret = new DateTime(dateTime);
        while (step < offset) {
            ret.plusDays(1);
            if (isOpeningDate(ret)) {
                continue;
            }
            step ++;
        }

        // 需要计算time
        DateTime retTime = TIME_FORMATTER.parseDateTime(time);
        ret = ret.withHourOfDay(retTime.getHourOfDay()).withMinuteOfHour(retTime.getMinuteOfHour()).withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return ret;
    }

    public DateTime buildDateTime(DateTime dateTime, DealRule dealRule) {
        return buildDateTime(dateTime, dealRule.getOffset(), dealRule.getTime());
    }

    public Boolean isOpeningDate(DateTime dateTime) {
        // 剔除周末
        if (dateTime.getDayOfWeek() == 6 || dateTime.getDayOfWeek() == 7) {
            return false;
        }
        if (holidays.contains(dateTime.toString(HOLIDAY_FORMATTER))) {
            return false;
        }
        return true;
    }

    private void fetchHoliday() throws StockTaskException {
        Set<String> set = new HashSet<>();
        for (Integer year : HOLIDAY_YEARS) {
            JSONObject jsonObject = httpHelper.get(String.format(HOLIDAY_URL, year), null, null,
                    JSONObject.class);
            if (!"0".equals(jsonObject.getString("status"))) {
                throw new StockTaskException(StockTaskResponseInfo.HOLIDAY_FETCH_FAILED);
            }
            JSONArray array = jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("holiday");
            for (int i = 0; i < array.size(); i ++) {
                JSONObject holidayObj = array.getJSONObject(i);
                IntStream.range(0, holidayObj.getJSONArray("list").size())
                        .mapToObj(t -> holidayObj.getJSONArray("list").getJSONObject(t))
                        .forEach(t -> set.add(t.getString("date")));
            }
        }
        holidays = set;
    }
}