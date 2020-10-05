package com.yanbin.stock.stocktaskservice.service.impl;

import com.emotibot.gemini.geminiutils.utils.RedisHelper;
import com.yanbin.stock.stocktaskservice.dao.StockJobDao;
import com.yanbin.stock.stocktaskservice.entity.StockJobEntity;
import com.yanbin.stock.stocktaskservice.mapper.StockTaskMapper;
import com.yanbin.stock.stocktaskservice.service.StockJobAdminService;
import com.yanbin.stock.stocktaskservice.service.StockJobManagerService;
import com.yanbin.stock.stocktaskutils.constants.StockTaskConstants;
import com.yanbin.stock.stocktaskutils.constants.StockTaskResponseInfo;
import com.yanbin.stock.stocktaskutils.exception.StockTaskException;
import com.yanbin.stock.stocktaskutils.pojo.StockJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanbinwang@emotibot.com
 * @date 2020/9/24 上午8:53
 */
@Service
public class StockJobManagerServiceImpl implements StockJobManagerService {

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    StockJobDao stockJobDao;

    @Autowired
    StockTaskMapper stockTaskMapper;

    @Autowired
    StockJobAdminService stockJobAdminService;

    /**
     * 如果是cronJob，就会自动写入到taskSchedulerHelper了
     *
     * 这里通知consul，由单独的线程来执行task update操作
     *
     * @param stockJob
     * @return
     */
    @Override
    public StockJob addStockJob(String appId, StockJob stockJob) {
        StockJobEntity stockJobEntity = stockTaskMapper.stockJobToStockJobEntity(stockJob);
        stockJobDao.save(stockJobEntity);
        stockJob = stockTaskMapper.stockJobEntityToStockJob(stockJobEntity);
        updateRedisForTask(stockJob.getAppId());
        return stockJob;
    }

    @Override
    public StockJob getStockJob(String appId, Integer id) {
        StockJobEntity stockJobEntity = stockJobDao.findOneById(id);
        if (stockJobEntity == null) {
            return null;
        }
        return stockTaskMapper.stockJobEntityToStockJob(stockJobEntity);
    }

    @Override
    public StockJob updateStockJob(String appId, Integer id, StockJob stockJob) throws StockTaskException {
        StockJobEntity stockJobEntity = stockJobDao.findOneById(id);
        if (stockJobEntity == null) {
            throw new StockTaskException(StockTaskResponseInfo.STOCK_TASK_JOB_NOT_EXIST);
        }
        StockJobEntity newStockJobEntity = stockTaskMapper.stockJobToStockJobEntity(stockJob);
        stockJobEntity.update(newStockJobEntity);
        stockJobDao.save(stockJobEntity);
        return stockTaskMapper.stockJobEntityToStockJob(stockJobEntity);
    }

    @Override
    public List<StockJob> getAllStockJob(String appId) {
        List<StockJobEntity> stockJobEntities = stockJobDao.findAllByAppId(appId);
        if (CollectionUtils.isEmpty(stockJobEntities)) {
            return null;
        }
        return stockJobEntities.stream().map(t -> stockTaskMapper.stockJobEntityToStockJob(t)).collect(Collectors.toList());
    }

    /**
     * 手动触发任务，不是定时任务
     *
     * @param id
     */
    @Override
    public void runStockJob(String appId, Integer id) throws StockTaskException {
        StockJob stockJob = getStockJob(appId, id);
        if (stockJob == null) {
            throw new StockTaskException(StockTaskResponseInfo.STOCK_TASK_JOB_NOT_EXIST);
        }
        stockJobAdminService.runStockJob(stockJob);
    }

    private void updateRedisForTask(String appId) {
        redisHelper.putHash(StockTaskConstants.STOCK_TASK_REDIS_KEY, appId, System.currentTimeMillis());
    }
}
