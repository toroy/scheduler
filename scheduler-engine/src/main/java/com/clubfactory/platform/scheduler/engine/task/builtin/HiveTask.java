package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.HiveParameters;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractYarnTask;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejiajun
 */
public class HiveTask extends AbstractYarnTask {

    public HiveTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected String fileExt() {
        return "sql";
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return HiveParameters.class;
    }

    @Override
    protected List<String> buildCommandList() throws Exception  {
        String appNameKey = this.getString("hive.app-name.key", "tez.app.name");
        String defaultAppName = String.format("Gaia-Hive-%s-%s", taskInfo.getName(), taskInfo.getId());
        return new ArrayList<>(HiveParameters.HiveArgsUtils.buildArgs(this.getParameter(), appNameKey, defaultAppName));
    }
}
