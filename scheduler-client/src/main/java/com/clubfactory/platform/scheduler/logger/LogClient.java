package com.clubfactory.platform.scheduler.logger;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.exception.BizException;
import com.clubfactory.platform.scheduler.logger.grpc.*;
import com.clubfactory.platform.scheduler.logger.vo.DBConnVO;
import com.clubfactory.platform.scheduler.logger.vo.LogVO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *  log client
 */
public class LogClient {

    private static  final Logger logger = LoggerFactory.getLogger(LogClient.class);

    private final ManagedChannel channel;
    private final LogViewServiceGrpc.LogViewServiceBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public LogClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true));
    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    LogClient(ManagedChannelBuilder<?> channelBuilder) {
        /**
         *  set max message read size
         */
        channelBuilder.maxInboundMessageSize(Integer.MAX_VALUE);
        channel = channelBuilder.build();
        blockingStub = LogViewServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     *  roll view log
     * @param path
     * @param skipLineNum
     * @param limit
     * @return
     */
    public String rollViewLog(String path,int skipLineNum,int limit) {
        logger.info("roll view log , path : {},skipLineNum : {} ,limit :{}", path, skipLineNum, limit);
        LogParameter pathParameter = LogParameter
                .newBuilder()
                .setPath(path)
                .setSkipLineNum(skipLineNum)
                .setLimit(limit)
                .build();
        RetStrInfo retStrInfo;
        try {
            retStrInfo = blockingStub.rollViewLog(pathParameter);
            return retStrInfo.getMsg();
        } catch (StatusRuntimeException e) {
            throw new BizException("日志读取服务连接失败 ," + e.getMessage());
        }
    }

    /**
     * @param path
     * @param skipLineNum
     * @param limit
     * @return
     */
    public LogVO rollViewLogVo(String path, int skipLineNum, int limit){
        return JSON.parseObject(rollViewLog(path,skipLineNum,limit), LogVO.class);
    }

    /**
     * @param path
     * @param rows
     * @return
     */
    public String getLastRowsLog(String path,int rows){
        logger.info("pull log path : {} last {} rows log ",path,rows);
        LastNRowsReq lastNRowsReq = LastNRowsReq.newBuilder().setRows(rows).setPath(path).build();
        RetStrInfo retStrInfo;
        try{
            retStrInfo = blockingStub.getLogOnLastNRows(lastNRowsReq);
            return retStrInfo.getMsg();
        }catch (StatusRuntimeException e){
            logger.error("pull  log  failed : " + e.getMessage(), e);
            throw new BizException("日志读取服务连接失败 ," + e.getMessage());
        }
    }

    /**
     * @param path
     * @param rows
     * @return
     */
    public LogVO getLastRowsLogVO(String path,int rows){
        return JSON.parseObject(getLastRowsLog(path,rows),LogVO.class);
    }


    /**
     * @param jdbcUrl
     * @param jdbcUser
     * @param jdbcPassword
     * @param dbType
     * @param timeout
     * @return
     */
    public String testDBConn(String jdbcUrl,String jdbcUser,String jdbcPassword,String dbType,int timeout){
        DBConnReq dbConnReq = DBConnReq.newBuilder().setJdbcUrl(jdbcUrl)
                .setJdbcUser(jdbcUser)
                .setJdbcPassword(jdbcPassword)
                .setJdbcType(dbType)
                .setTimeout(timeout)
                .build();
        RetStrInfo retStrInfo;
        try{
            retStrInfo = blockingStub.testDBConn(dbConnReq);
            return retStrInfo.getMsg();
        }catch (StatusRuntimeException e){
            logger.error("Worker连接失败 ,暂无连接状态信息 : " + e.getMessage(), e);
            throw new BizException("Worker连接失败 ,暂无连接状态信息");
        }
    }


    /**
     * @param jdbcUrl
     * @param jdbcUser
     * @param jdbcPassword
     * @param dbType
     * @param timeout
     * @return
     */
    public DBConnVO testDBConnVO(String jdbcUrl,String jdbcUser,String jdbcPassword,String dbType,int timeout){
        return JSON.parseObject(testDBConn(jdbcUrl,jdbcUser,jdbcPassword,dbType,timeout),DBConnVO.class);
    }

    /**
     * @param dirPath
     * @param nDays
     * @return
     */
    public void clearExpireLog(String dirPath,int nDays) {
        logger.info("clear expire {} days log from dir {}", nDays, dirPath);
        ExpireLogReq expireLogReq = ExpireLogReq.newBuilder().setDir(dirPath).setNDays(nDays).build();
        try {
            blockingStub.expireLogNdaysAgo(expireLogReq);
        } catch (StatusRuntimeException e) {
            logger.error("clear expire log failed : " + e.getMessage(), e);
        }
    }

    /**
     * @param nDays
     * @return
     */
    public void clearExpireLog(int nDays) {
        logger.info("clear expire {} days log from task log dir", nDays);
        ExpireLogReq expireLogReq = ExpireLogReq.newBuilder().setDir("").setNDays(nDays).build();
        try {
            blockingStub.expireLogNdaysAgo(expireLogReq);
        } catch (StatusRuntimeException e) {
            logger.error("clear expire log failed : " + e.getMessage(), e);
        }
    }


    /**
     *  view all log
     * @param path
     * @return
     */
    public String viewLog(String path) {
        logger.info("view log path : {}",path);

        PathParameter pathParameter = PathParameter.newBuilder().setPath(path).build();
        RetStrInfo retStrInfo;
        try {
            retStrInfo = blockingStub.viewLog(pathParameter);
            return retStrInfo.getMsg();
        } catch (StatusRuntimeException e) {
            throw new BizException("日志读取服务连接失败 ," + e.getMessage());
        }
    }

    /**
     *  get log bytes
     * @param path
     * @return
     */
    public byte[] getLogBytes(String path) {
        logger.info("get log bytes {}",path);

        PathParameter pathParameter = PathParameter.newBuilder().setPath(path).build();
        RetByteInfo retByteInfo;
        try {
            retByteInfo = blockingStub.getLogBytes(pathParameter);
            return retByteInfo.getData().toByteArray();
        } catch (StatusRuntimeException e) {
            logger.error("get log bytes failed : " + e.getMessage(), e);
            throw new BizException("日志读取服务连接失败 ," + e.getMessage());
        }
    }
}