package com.clubfactory.platform.scheduler.spi.plugin;

import com.tencentcloudapi.emr.v20190103.models.*;
import com.clubfactory.platform.scheduler.core.model.AppInfo;
import com.clubfactory.platform.scheduler.core.monitor.YarnTaskMonitor;
import com.clubfactory.platform.scheduler.core.utils.SpringBean;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
//import com.clubfactory.platform.scheduler.spi.launcher.TransientEmrLauncher;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.util.List;

import static com.clubfactory.platform.scheduler.common.Constants.*;
//import static com.clubfactory.platform.scheduler.spi.constants.AwsEmrConstants.*;

/**
 * @author xiejiajun
 */
public abstract class AbstractEmrTask extends AbstractTask {


    private List<Configuration> configurations;


    public AbstractEmrTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    //TODO
//    @Override
//    protected void createLauncher() {
//        this.taskLauncher = new TransientEmrLauncher(this, this.logger, this.stateTracker);
//    }

    @Override
    protected void downloadScriptToExecDir() {}

    @Override
    public void preHandle() throws Exception {
        super.preHandle();
//        this.initConfig();
//        this.appendEmrConfig(this.configurations);
    }

    /**
     * 集群公共配置
     *
     * @return
     */
//    protected final void initConfig() {
//        this.configurations = Lists.newLinkedList();
//        Configuration emrfsConfig = new Configuration();
//        emrfsConfig.setClassification("emrfs-site");
//        emrfsConfig.addPropertiesEntry("fs.s3.consistent.retryPeriodSeconds", "10")
//                .addPropertiesEntry("fs.s3.consistent", this.getString(FS_S3_CONSISTENT, DEFAULT_FS_S3_CONSISTENT))
//                .addPropertiesEntry("fs.s3.consistent.retryCount", "3")
//                .addPropertiesEntry("fs.s3.consistent.metadata.tableName",
//                        this.getString(AWS_EMRFS_METADATA_TABLE, DEFAULT_AWS_EMRFS_METADATA_TABLE)
//                );
//        this.configurations.add(emrfsConfig);
//
//        Configuration yarnSchedulerConfig = new Configuration()
//                .withClassification("capacity-scheduler")
//                .addPropertiesEntry("yarn.scheduler.capacity.resource-calculator",
//                        "org.apache.hadoop.yarn.util.resource.DominantResourceCalculator")
//                .addPropertiesEntry("yarn.scheduler.minimum-allocation-mb", "1024");
//        this.configurations.add(yarnSchedulerConfig);
//    }

    /**
     * 获取EMR集群配置
     *
     * @return
     */
//    public final List<Configuration> configurations(){
//        return this.configurations;
//    }

    /**
     * 补充配置
     * @param emrConfigs
     */
//    public abstract void appendEmrConfig(List<Configuration> emrConfigs);

    /**
     * 用于从实现类中获取step类型（Hive、Spark、SparkStreaming)
     *
     * @return
     */
    public abstract String stepType();

    /**
     * EMR集群需要启动的服务
     *
     * @return
     */
//    public abstract List<Application> applications();

    /**
     * 将Streaming应用信息添加到monitor
     *
     * @param appInfo
     * @param masterDNS
     */
    public void addAppMonitor(AppInfo appInfo,String masterDNS) {
        logger.info("Launch Emr Streaming Task:{} on {}, current state: {}",appInfo.getAppId()
                ,masterDNS,appInfo.getAppState());
        YarnTaskMonitor yarnTaskMonitor = SpringBean.getBean(YarnTaskMonitor.class);
        try {
            Integer failoverCnt = this.getInt(STREAMING_TASK_FAILOVER_RETRY_NUM, DEFAULT_STREAMING_TASK_FAILOVER_RETRY_NUM);
            yarnTaskMonitor.monitor(taskInfo, appInfo.getAppId(), masterDNS, failoverCnt);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }


}
