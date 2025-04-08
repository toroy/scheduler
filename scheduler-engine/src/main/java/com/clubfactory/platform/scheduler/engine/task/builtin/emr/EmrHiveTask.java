package com.clubfactory.platform.scheduler.engine.task.builtin.emr;

//import com.amazonaws.services.elasticmapreduce.model.Application;
//import com.amazonaws.services.elasticmapreduce.model.Configuration;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.HiveParameters;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractEmrTask;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejiajun
 */
public class EmrHiveTask extends AbstractEmrTask {

    public EmrHiveTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return HiveParameters.class;
    }

//    @Override
//    public void appendEmrConfig(List<Configuration> emrConfigs) {
//        Configuration hiveConfig = new Configuration()
//                .withClassification("hive-site")
//                .addPropertiesEntry("hive.metastore.client.factory.class",
//                        "com.amazonaws.glue.catalog.metastore.AWSGlueDataCatalogHiveClientFactory");
//        emrConfigs.add(hiveConfig);
//    }
//
//    @Override
//    public List<Application> applications() {
//        List<Application> applications = Lists.newArrayList();
//        applications.add(new Application().withName("Ganglia"));
//        applications.add(new Application().withName("Hive"));
//        return applications;
//    }

    @Override
    public String stepType() {
        return "hive";
    }

    @Override
    protected String fileExt() {
        return "sql";
    }

    @Override
    protected List<String> buildCommandList() throws Exception  {
        String appNameKey = this.getString("hive.app-name.key", "tez.app.name");
        String defaultAppName = String.format("Gaia-Hive-%s-%s", taskInfo.getName(), taskInfo.getId());
        return new ArrayList<>(HiveParameters.HiveArgsUtils.buildArgs(this.getParameter(), appNameKey, defaultAppName));
    }

}
