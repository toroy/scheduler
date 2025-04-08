package com.clubfactory.platform.scheduler.server.worker;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.service.ICommonService;
import com.clubfactory.platform.scheduler.core.thread.Stopper;
import com.clubfactory.platform.scheduler.core.thread.ThreadUtils;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.utils.PropertyUtils;
import com.clubfactory.platform.scheduler.core.vo.SysConfigVO;
import com.clubfactory.platform.scheduler.dal.enums.CommonStatus;
import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import com.clubfactory.platform.scheduler.dal.enums.MachineTypeEnum;
import com.clubfactory.platform.scheduler.dal.po.Machine;
import com.clubfactory.platform.scheduler.server.common.AbstractServer;
import com.clubfactory.platform.scheduler.server.config.CommonProperties;
import com.clubfactory.platform.scheduler.server.config.WorkerProperties;
import com.clubfactory.platform.scheduler.server.config.ZookeeperProperties;
import com.clubfactory.platform.scheduler.engine.dao.DataAccessWrapper;
import com.clubfactory.platform.scheduler.engine.mgr.TaskRunnerMgr;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * worker server
 * @author xiejiajun
 */
@Component
public class WorkerServer extends AbstractServer {

    private static final Logger logger = LoggerFactory.getLogger(WorkerServer.class);


    private  TaskRunnerMgr taskRunnerMgr = null;


    @Resource(name = "commonService")
    @Setter
    private ICommonService commonService;


    @Autowired
    @Setter
    private WorkerProperties workerProperties;

    @Autowired
    @Setter
    private ZookeeperProperties zookeeperProperties;

    @Autowired
    @Setter
    private CommonProperties commonProperties;

    private DataAccessWrapper dataAccessWrapper;


    /**
     * heartbeat executor service
     */
    private  ScheduledExecutorService heartbeatWorkerService;




    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().setName("Worker-Main-Thread");

        WorkerServer workerServer = new WorkerServer();
        workerServer.setCommonService(commonService);
        workerServer.setZookeeperProperties(zookeeperProperties);
        workerServer.setWorkerProperties(workerProperties);
        workerServer.setCommonProperties(commonProperties);

        workerServer.start();

        logger.info("worker server started");

        workerServer.awaitTermination();
    }


    public void start(){
        try {
            dataAccessWrapper = DataAccessWrapper.getInstance();
            dataAccessWrapper.init(commonService);
            // 服务启动，更新DB中机器状态为启用
            this.updateWorkerStatus(CommonStatus.ENABLED);


            // 初始化PropertyUtils
            Properties props = commonProperties.getProperties();
            props.putAll(this.refreshSysConfig());
            PropertyUtils.init(props);

            // 初始化taskRunnerMgr
            this.taskRunnerMgr = TaskRunnerMgr.getTaskRunnerMgr();
            this.taskRunnerMgr.setExecutorConfig(workerProperties.buildExecutorConfig());
            this.taskRunnerMgr.setZookeeperProperties(zookeeperProperties.buildZkConfig());
            this.taskRunnerMgr.setStoppable(this);
            this.taskRunnerMgr.init();

            heartBeatInterval = workerProperties.getHeartbeatInterval();
            heartbeatWorkerService = ThreadUtils.newDaemonThreadScheduledExecutor("Worker-Heartbeat-Thread-Executor",
                    Constants.DEFAULT_WORKER_HEARTBEAT_THREAD_NUM);

            Thread sysConfigRefreshThread = new Thread(this.createSysConfigRefreshThread());
            sysConfigRefreshThread.setName("sys-config-refresh-thread");
            sysConfigRefreshThread.start();

        }catch (Exception e){
            logger.error("init worker failed",e);
            System.exit(1);
        }

        heartbeatWorkerService.scheduleAtFixedRate(
                () -> {
                    if (StringUtils.isEmpty(taskRunnerMgr.getWorkerZNode())){
                        logger.error("worker send heartbeat to zk failed");
                    }
                    taskRunnerMgr.heartBeatForZk(taskRunnerMgr.getWorkerZNode());
                }
                ,5,heartBeatInterval,TimeUnit.SECONDS
        );
        /**
         * register hooks, which are called before the process exits
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.warn("worker server stopped,set status to disabled ...");
                updateWorkerStatus(CommonStatus.DISABLED);
                if (Stopper.isRunning()){
                    Stopper.stop();
                }
            }
        }));
    }

    @Override
    public void stop(String cause) {
        try{
            if (Stopper.isStoped()){
                return;
            }
            logger.info("worker server is stopping ..., cause : {}", cause);
            Stopper.stop();
            Thread.sleep(3000L);

            heartbeatWorkerService.shutdownNow();
            taskRunnerMgr.close();

            logger.info("server env clear finish , start notify main thread ......");
            // notify
            synchronized (lock) {
                terminated = true;
                lock.notifyAll();
                logger.info("notify main thread, terminate the worker server ......");
            }
            System.exit(0);
        }catch (Exception e){
            logger.error("worker server stop exception : " + e.getMessage(), e);
            System.exit(-1);
        }


    }

    /**
     * 更新DB中 worker状态
     * @param status
     */
    private void updateWorkerStatus(CommonStatus status){
        Machine machine = new Machine();
        machine.setType(MachineTypeEnum.WORKER);
        machine.setIp(OSUtils.getHost());
        machine.setStatus(status);
        dataAccessWrapper.changeMachineStatus(machine);
    }


    /**
     * 刷新Worker配置
     */
    private Properties refreshSysConfig() {
        Properties props = new Properties();
        try {
            List<SysConfigVO> sysConfigVOS = dataAccessWrapper.listValidConf(OSUtils.getHost(), ConfigType.WORKER);
            if (CollectionUtils.isEmpty(sysConfigVOS)) {
                return props;
            }
            synchronized (this) {
                for (SysConfigVO sysConfigVO : sysConfigVOS) {
                    props.put(sysConfigVO.getParamKey(), sysConfigVO.getParamValue());
                }
                logger.info("refresh sys configs success");
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return props;
    }

    /**
     * 系统配置刷新线程
     * @return
     */
    private Runnable createSysConfigRefreshThread() {
        return () -> {
            while (Stopper.isRunning()) {
                try {
                    Thread.sleep(Constants.WORKER_CONFIG_REFRESH_INTERVAL);
                } catch (InterruptedException e) {
                    logger.error("interrupted exception", e);
                }
                try {
                    Properties props = this.refreshSysConfig();
                    PropertyUtils.refreshConfig(props);
                } catch (Exception e) {
                    logger.error(String.format("刷新系统配置失败:%s",e.getMessage()),e);
                }
            }
        };
    }
}
