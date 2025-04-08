package com.clubfactory.platform.scheduler.spi.plugin;

import com.clubfactory.platform.scheduler.core.monitor.YarnTaskMonitor;
import com.clubfactory.platform.scheduler.core.utils.SpringBean;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.exception.TaskException;
import com.clubfactory.platform.scheduler.spi.utils.ProcessUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import java.util.List;

import static com.clubfactory.platform.scheduler.common.Constants.*;

/**
 * @author xiejiajun
 */
public abstract class AbstractYarnTask extends AbstractTask {


    public AbstractYarnTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    public void cancelApplication(boolean status) throws Exception {
        super.cancelApplication(status);
        if (status && taskInfo != null){
            ProcessUtils.killYarnJob(taskInfo);
        }
    }

    @Override
    public int ensureState(int exitStatusCode){
        if (isStreamTask()) {
            return exitStatusCode;
        }
        // block until yarn application finish
        return ProcessUtils.ensureYarnState(this.taskInfo,this.logger,exitStatusCode);
    }

    @Override
    public void postHandle() throws Exception {
        super.postHandle();
        if (this.isStreamTask()) {
            this.saveYarnAppInfo();
        }
    }


    /**
     * 将Yarn AppId添加到监控队列
     */
    private void saveYarnAppInfo() throws TaskException {
        List<String> appIds = ProcessUtils.findAppIdsFromLogFile(taskInfo.getLogPath());
        if (CollectionUtils.isEmpty(appIds)){
            throw new TaskException("applicationId为空，任务未启动成功");
        }
        YarnTaskMonitor yarnTaskMonitor = SpringBean.getBean(YarnTaskMonitor.class);
        try {
            Integer failoverCnt = this.getInt(STREAMING_TASK_FAILOVER_RETRY_NUM, DEFAULT_STREAMING_TASK_FAILOVER_RETRY_NUM);
            yarnTaskMonitor.monitor(taskInfo, appIds.get(0), failoverCnt);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

}
