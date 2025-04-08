package com.clubfactory.platform.scheduler.engine.mgr;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xiejiajun
 */
@Slf4j
public class LogicMemoryMgr {

    private static LogicMemoryMgr INSTANCE;

    /**
     * 当前被逻辑占用的内存
     */
    private final AtomicLong currentUsedMemory = new AtomicLong(0);

    /**
     * 系统可用内存
     */
    private Long availableMemory;


    private LogicMemoryMgr(){
        long totalMemory;
        try {
            totalMemory = OSUtils.totalMemoryBytes();
        }catch (Exception e){
            int memoryTotal = PropertyUtils.getInt(Constants.WORKER_TOTAL_MEMORY,2);
            totalMemory = this.gb2Bytes(memoryTotal);
        }
        double reservedMemoryMB = PropertyUtils.getDouble(Constants.WORKER_RESERVED_MEMORY,
                Constants.DEFAULT_WORKER_RESERVED_MEMORY) * 1024;
        long reservedMemoryBytes = mb2Bytes(reservedMemoryMB);
        if (totalMemory > reservedMemoryBytes) {
            this.availableMemory = totalMemory - reservedMemoryBytes;
        }else {
            // 至少得预留700MB
            this.availableMemory = totalMemory - mb2Bytes(700);
        }

    }

    public static LogicMemoryMgr getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LogicMemoryMgr();
        }
        return INSTANCE;
    }



    public double currentAssignedMemoryGB(){
        return this.currentUsedMemory.get() / 1024.0 / 1024 / 1024;
    }

    public double availableMemoryGB(){
        return this.availableMemory / 1024.0 / 1024 / 1024;
    }

    /**
     * 释放内存
     * @param mbMemSize
     */
    public void releaseMemory(int mbMemSize){
        long freeMemBytes = -1 * mb2Bytes(mbMemSize);
        synchronized (this) {
            long usedMemory = this.currentUsedMemory.addAndGet(freeMemBytes);
            if (usedMemory < 0){
                this.currentUsedMemory.set(0L);
            }
        }
        log.info("release memory {} MB success",mbMemSize);

    }

    /**
     * 检查内存是否够用，若足够则申请内存并返回true，否则false
     * @param mbMemSize
     * @return
     */
    public boolean checkAndAcquireMemoryGB(int mbMemSize){
        long acquireMemBytes = mb2Bytes(mbMemSize);
        synchronized (currentUsedMemory) {
            boolean isEnough = (this.currentUsedMemory.get() + acquireMemBytes) <= this.availableMemory;
            if (isEnough) {
                this.currentUsedMemory.addAndGet(acquireMemBytes);
                log.info("acquire memory {} MB success",mbMemSize);
                return true;
            }
            return false;
        }
    }

    /**
     * 根据作业类型检查内存是否够用，若足够则申请内存并返回true，否则false
     * @param jobCategory
     * @param jobType
     * @return
     */
    public boolean checkAndAcquireMemoryGB(JobCategoryEnum jobCategory, String jobType){
        int mbMemSize = getTaskMemoryPeak(jobCategory,jobType);
        return this.checkAndAcquireMemoryGB(mbMemSize);
    }

    /**
     * 根据作业类型检查内存是否够用，若足够则申请内存并返回true，否则false
     * @param jobCategory
     * @param jobType
     * @return
     */
    public void releaseMemory(JobCategoryEnum jobCategory, String jobType){
        int mbMemSize = getTaskMemoryPeak(jobCategory,jobType);
        this.releaseMemory(mbMemSize);
    }


    /**
     * 获取各种任务得内存消耗峰值，单位：MB
     * 注意：这是一个人为估计得逻辑值，不会真正监控到任务运行过程中的波动
     * @param jobCategory
     * @param jobType
     * @return
     */
    public int getTaskMemoryPeak(JobCategoryEnum jobCategory,String jobType){
        return this.getTaskMemoryPeak(null,jobCategory,jobType);
    }

    /**
     * 获取各种任务得内存消耗峰值，单位：MB
     * 注意：这是一个人为估计得逻辑值，不会真正监控到任务运行过程中的波动
     * @param taskVO
     * @return
     */
    public int getTaskMemoryPeak(TaskVO taskVO){
        if (taskVO == null || taskVO.isRunOnTmpEmr()){
            return -1;
        }
        JobCategoryEnum jobCategory = taskVO.getCategory();
        String jobType = taskVO.getType();
        Map<String,String> jobConf = taskVO.getJobConf();
        return this.getTaskMemoryPeak(jobConf,jobCategory,jobType);
    }

    /**
     * 获取各种任务得内存消耗峰值，单位：MB
     * 注意：这是一个人为估计得逻辑值，不会真正监控到任务运行过程中的波动
     * @param jobConf
     * @param jobCategory
     * @param jobType
     * @return
     */
    public int getTaskMemoryPeak(Map<String,String> jobConf,JobCategoryEnum jobCategory,String jobType){
        if (jobCategory == null){
            return 1024;
        }
        int memoryPeak;
        String taskMemoryPeakKey;
        switch (jobCategory){
            case CAL:
                if (jobType == null){
                    return 1024;
                }
                taskMemoryPeakKey = String.format("%s.task.memory.peak",jobType.toLowerCase());
                memoryPeak = getTaskMemoryPeak(jobConf,taskMemoryPeakKey,1024);
                // PRESTO任务配置为512M
                memoryPeak = getPrestoMemoryPeak(jobConf, jobType, memoryPeak);
                break;
            case REFLUE:
            case COLLECT:
                taskMemoryPeakKey = "etl.task.memory.peak";
                memoryPeak = getTaskMemoryPeak(jobConf, taskMemoryPeakKey,3584);
                // PRESTO任务配置为512M
                memoryPeak = getPrestoMemoryPeak(jobConf, jobType, memoryPeak);
                break;
            default:
                memoryPeak = 1024;
        }
        return memoryPeak;
    }

    private int getPrestoMemoryPeak(Map<String, String> jobConf, String jobType, int memoryPeak) {
        if (null != jobType && jobType.equalsIgnoreCase("PRESTO")) {
            String taskMemoryPeakKey = "presto.etl.task.memory.peak";
            memoryPeak = getTaskMemoryPeak(jobConf,taskMemoryPeakKey,256);
        }
        return memoryPeak;
    }

    private long gb2Bytes(int gbSize){
        return gbSize * 1024 * 1024 * 1024L;
    }

    private long mb2Bytes(int mbSize){
        return mbSize * 1024 * 1024L;
    }

    private long mb2Bytes(double mbSize){
        return new Double(mbSize).longValue() * 1024 * 1024;
    }

    private int getTaskMemoryPeak(Map<String,String> jobConf,String taskMemoryPeakKey,int defaultValue){
        if (MapUtils.isNotEmpty(jobConf)) {
            int memoryPeek = -1;
            String memoryPeekString = jobConf.get(taskMemoryPeakKey);
            if (StringUtils.isNotBlank(memoryPeekString)) {
                try {
                    memoryPeek = Integer.parseInt(memoryPeekString);
                } catch (Exception ignore) {}
            }
            if (memoryPeek != -1) {
                return memoryPeek;
            }
        }
        return PropertyUtils.getInt(defaultValue, taskMemoryPeakKey);
    }


}
