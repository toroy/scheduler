package com.clubfactory.platform.scheduler.core.utils;


import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.model.WorkerInfo;

import java.util.Date;

/**
 *  heartbeat for ZK reigster res info
 */
public class ResInfo {

    /**
     *  cpuUsage
     */
    private double cpuUsage;

    /**
     *  memoryUsage
     */
    private double memoryUsage;

    /**
     * loadAverage
     */
    private double loadAverage;

    public ResInfo(){}

    public ResInfo(double cpuUsage , double memoryUsage){
        this.cpuUsage = cpuUsage ;
        this.memoryUsage = memoryUsage;
    }

    public ResInfo(double cpuUsage, double memoryUsage, double loadAverage) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.loadAverage = loadAverage;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public double getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(double loadAverage) {
        this.loadAverage = loadAverage;
    }

    /**
     * get CPU and memory usage
     * add cpu load average by lidong for service monitor
     * @return
     */
    public static String getResInfoJson(){
        ResInfo resInfo = new ResInfo(OSUtils.cpuUsage(), OSUtils.memoryUsage(),OSUtils.loadAverage());
        return JSONUtils.toJson(resInfo);
    }


    /**
     * get CPU and memory usage
     * @return
     */
    public static String getResInfoJson(double cpuUsage , double memoryUsage){
        ResInfo resInfo = new ResInfo(cpuUsage,memoryUsage);
        return JSONUtils.toJson(resInfo);
    }


    public static String getHeartBeatInfo(Date now){
        return buildHeartbeatForZKInfo(OSUtils.getHost(),
                OSUtils.getProcessID(),
                OSUtils.cpuUsage(),
                OSUtils.memoryUsage(),
                DateUtils.dateToString(now),
                DateUtils.dateToString(now));

    }

    /**
     * build heartbeat info for zk
     * @param host
     * @param port
     * @param cpuUsage
     * @param memoryUsage
     * @param createTime
     * @param lastHeartbeatTime
     * @return
     */
    public static String buildHeartbeatForZKInfo(String host , int port ,
                                         double cpuUsage , double memoryUsage,
                                         String createTime,String lastHeartbeatTime){

        return host + Constants.COMMA + port + Constants.COMMA
                + cpuUsage + Constants.COMMA
                + memoryUsage + Constants.COMMA
                + createTime + Constants.COMMA
                + lastHeartbeatTime;
    }

    /**
     * parse heartbeat info for zk
     * @param heartBeatInfo
     * @return
     */
    public static WorkerInfo parseHeartbeatForZKInfo(String heartBeatInfo){
        WorkerInfo workerInfo =  null;
        String[] masterArray = heartBeatInfo.split(Constants.COMMA);
        if(masterArray.length != 6){
            return workerInfo;

        }
        workerInfo = new WorkerInfo();
        workerInfo.setIp(masterArray[0]);
        return workerInfo;
    }

}
