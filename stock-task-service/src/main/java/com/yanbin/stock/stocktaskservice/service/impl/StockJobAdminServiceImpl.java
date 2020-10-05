package com.yanbin.stock.stocktaskservice.service.impl;

import com.emotibot.gemini.geminiutils.constants.task.TaskStatus;
import com.emotibot.gemini.geminiutils.utils.RedisHelper;
import com.emotibot.gemini.geminiutils.utils.TaskSchedulerHelper;
import com.emotibot.gemini.geminiutils.utils.UuidUtils;
import com.yanbin.stock.stocktaskservice.dao.StockActionExecuteMsgDao;
import com.yanbin.stock.stocktaskservice.entity.StockActionExecuteMsgEntity;
import com.yanbin.stock.stocktaskservice.mapper.StockTaskMapper;
import com.yanbin.stock.stocktaskservice.service.StockJobAdminService;
import com.yanbin.stock.stocktaskservice.service.StockJobManagerService;
import com.yanbin.stock.stocktaskservice.utils.StockActionHelper;
import com.yanbin.stock.stocktaskutils.action.StockAction;
import com.yanbin.stock.stocktaskutils.constants.StockActionType;
import com.yanbin.stock.stocktaskutils.constants.StockTaskConstants;
import com.yanbin.stock.stocktaskutils.pojo.StockActionContext;
import com.yanbin.stock.stocktaskutils.pojo.StockActionExecuteMsg;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/25 上午8:43
 *
 * 股票任务触发有两种形式，
 * 一种的cronJob（通过taskSchedule管理）,只有通过stockJob生成的actionMsg才会创建taskId
 * 一种是定时任务（写入到redis中，判断是否到达触发条件）
 *
 * 这两种均通过StockActionRunner做多线程执行
 */
@Slf4j
@Service
public class StockJobAdminServiceImpl implements StockJobAdminService {

    @Autowired
    TaskSchedulerHelper taskSchedulerHelper;

    @Autowired
    StockActionHelper stockActionHelper;

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    StockActionExecuteMsgDao stockActionExecuteMsgDao;

    @Autowired
    StockJobManagerService stockJobManagerService;

    @Autowired
    StockTaskMapper stockTaskMapper;

    @Value("${executor.debug}")
    private Boolean debug;

    @Value("${executor.maxThreadNum}")
    private Integer maxThreadNum;

    private Map<String, Long> taskUpdateTimestampMap = new HashMap<>();

    private ExecutorService executorService;

    /**
     * 需要将数据库中处于Running的task获取，写入到队列中，做
     *
     */
    @PostConstruct
    private void init() {
        // 这里需要block住
        executorService = new ThreadPoolExecutor(maxThreadNum,
                maxThreadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 定时监听redis中stock task中的变动，如果变动，需要加载
     *
     */
    @Scheduled(fixedRate = 1000)
    public void syncStockJob() {
        Map<Object, Object> map = redisHelper.getAllHash(StockTaskConstants.STOCK_TASK_REDIS_KEY);
        for (Object key : map.keySet()) {
            String appId = (String) key;
            Long timestamp = (Long) map.get(key);
            if (taskUpdateTimestampMap.containsKey(appId) && timestamp.equals(taskUpdateTimestampMap.get(appId))) {
                continue;
            }
            taskUpdateTimestampMap.put(appId, timestamp);
            try {
                log.info("start update connector task for {}", appId);
                taskSchedulerHelper.removeCronByPrefix(appId);
                List<StockJob> stockJobs = stockJobManagerService.getAllStockJob(appId);
                if (!CollectionUtils.isEmpty(stockJobs)) {
                    stockJobs.stream().filter(t -> t.getEnable() && !StringUtils.isEmpty(t.getCron()))
                            .forEach(t -> taskSchedulerHelper.startCron(buildCronTaskId(t.getAppId(), t.getId()),
                                    t.getCron(), new StockActionRunner(t)));
                }
                log.info("end update connector task for {}", appId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * 有一些定时触发的任务需要执行，例如到点后购买股票的动作等，也需要将所有需要定点的股票存放在redis中，定期去扫描，一旦满足时间条件后。
     * 立即执行
     *
     */
    @Scheduled(fixedRate = 10)
    public void pollingTimerAction() {
        Map<Object, Object> map = redisHelper.getAllHash(StockTaskConstants.STOCK_TIMER_ACTION_KEY);
        for (Object key : map.keySet()) {
            StockActionExecuteMsg stockActionExecuteMsg = (StockActionExecuteMsg) map.get(key);
            StockAction stockAction = buildActionFromExecutorMsg(null, stockActionExecuteMsg);
            if (stockAction.getExecuteTime() == null || stockAction.getExecuteTime() <= System.currentTimeMillis()) {
                // TODO 思考如何将一系列的action的结果汇总在一起
                executorService.submit(new StockActionRunner(stockActionExecuteMsg));
                // 从定时任务中移除
                redisHelper.delHash(StockTaskConstants.STOCK_TIMER_ACTION_KEY, (String) key);
            }
        }
    }

    @Override
    public void runStockJob(StockJob stockJob) {
        StockActionExecuteMsg stockActonExecuteMsg = StockActionExecuteMsg.builder().appId(stockJob.getAppId())
                .jobId(stockJob.getId()).taskId(UuidUtils.buildUuid()).action(stockJob.getStockAction())
                .context(new StockActionContext()).status(TaskStatus.TODO).build();
        insertStockActionMsg(stockActonExecuteMsg);
    }

    /**
     * 向redis中写入任务，该任务是一次性任务
     *
     * @param stockActionExecuteMsg
     */
    @Override
    public void scheduleStockAction(StockActionExecuteMsg stockActionExecuteMsg) {
        
    }

    private Boolean insertStockActionMsg(StockActionExecuteMsg stockActionExecuteMsg) {
        StockActionExecuteMsgEntity stockActionExecuteMsgEntity =
                stockTaskMapper.stockActionExecuteMsgToStockActionExecuteMsgEntity(stockActionExecuteMsg);
        stockActionExecuteMsgDao.save(stockActionExecuteMsgEntity);
        stockActionExecuteMsg = stockTaskMapper.stockActionExecuteMsgEntityToStockActionExecuteMsg(stockActionExecuteMsgEntity);
        if (debug) {
            return executeStockActionExecuteMsg(stockActionExecuteMsg);
        }
        // TODO 通过消息队列
        return false;
    }

    private StockActionExecuteMsg updateStockActionExecuteMsgStatus(Integer id, TaskStatus taskStatus) {
        StockActionExecuteMsgEntity stockActionExecuteMsgEntity = stockActionExecuteMsgDao.findOneById(id);
        if (stockActionExecuteMsgEntity == null) {
            return null;
        }
        stockActionExecuteMsgEntity.setStatus(taskStatus);
        stockActionExecuteMsgDao.save(stockActionExecuteMsgEntity);
        return stockTaskMapper.stockActionExecuteMsgEntityToStockActionExecuteMsg(stockActionExecuteMsgEntity);
    }

    public Boolean executeStockActionExecuteMsg(StockActionExecuteMsg stockActionExecuteMsg) {
        StockJob stockJob = stockJobManagerService.getStockJob(stockActionExecuteMsg.getAppId(), stockActionExecuteMsg.getJobId());
        if (stockJob == null) {
            updateStockActionExecuteMsgStatus(stockActionExecuteMsg.getId(), TaskStatus.FAIL);
            return false;
        }
        StockAction action = buildActionFromExecutorMsg(stockJob.getType(), stockActionExecuteMsg);
        if (debug) {
            executeAction(action, stockActionExecuteMsg);
        }
        return true;
    }

    private StockAction buildActionFromExecutorMsg(StockActionType stockActionType, StockActionExecuteMsg stockActionExecuteMsg) {
        // 优先从action结构中获取
        StockActionType actionType = null;
        if (stockActionExecuteMsg.getAction() != null) {
            actionType = StockActionType.buildActionType(stockActionExecuteMsg.getAction().getString(StockTaskConstants.ACTION_TYPE_KEY));
        }
        if (actionType == null) {
            actionType = stockActionType;
        }
        if (actionType == null) {
            log.error("action type is not found");
            return null;
        }
        return stockActionHelper.buildStockAction(actionType, stockActionExecuteMsg.getAction());
    }

    private void executeAction(StockAction stockAction, StockActionExecuteMsg stockActionExecuteMsg) {
        updateStockActionExecuteMsgStatus(stockActionExecuteMsg.getId(), TaskStatus.RUNNING);
        try {
            stockActionHelper.executeAction(stockAction, stockActionExecuteMsg);
            stockActionExecuteMsgDao.deleteById(stockActionExecuteMsg.getId());
        } catch (Exception e) {
            e.printStackTrace();
            updateStockActionExecuteMsgStatus(stockActionExecuteMsg.getId(), TaskStatus.FAIL);
        }
    }

    private String buildCronTaskId(String appId, Integer taskId) {
        return String.format("%s_%d", appId, taskId);
    }

    class StockActionRunner implements Runnable {

        private StockActionExecuteMsg stockActionExecuteMsg;
        private StockJob stockJob;

        public StockActionRunner(StockActionExecuteMsg stockActionExecuteMsg) {
            this.stockActionExecuteMsg = stockActionExecuteMsg;
        }

        public StockActionRunner(StockJob stockJob) {
            this.stockJob = stockJob;
        }

        @Override
        public void run() {
            if (stockJob != null) {
                runStockJob(stockJob);
            } else if (stockActionExecuteMsg != null) {
                insertStockActionMsg(stockActionExecuteMsg);
            }
        }
    }
}
