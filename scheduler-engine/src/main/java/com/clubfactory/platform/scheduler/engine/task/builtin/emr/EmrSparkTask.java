package com.clubfactory.platform.scheduler.engine.task.builtin.emr;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.SparkParameters;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.SparkTaskType;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractEmrTask;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejiajun
 */
public class EmrSparkTask extends AbstractEmrTask {

    protected static final String SPARK_COMMAND = "spark-submit";

    public EmrSparkTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return SparkParameters.class;
    }

//    @Override
//    public void appendEmrConfig(List<Configuration> emrConfigs) {
//        Configuration hiveConfig = new Configuration()
//                .withClassification("spark-hive-site")
//                .addPropertiesEntry("hive.metastore.client.factory.class",
//                        "com.amazonaws.glue.catalog.metastore.AWSGlueDataCatalogHiveClientFactory");
//        emrConfigs.add(hiveConfig);
//    }
//
//    @Override
//    public List<Application> applications() {
//        List<Application> applications = Lists.newArrayList();
//        applications.add(new Application().withName("Ganglia"));
//        applications.add(new Application().withName("Spark"));
//        return applications;
//    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        List<String> commandList = new ArrayList<>();
        String defaultSparkAppName = String.format("'Gaia-Spark-%s-%s'", taskInfo.getName(), taskInfo.getId());
        commandList.add(SPARK_COMMAND);
        commandList.add("\\\n");
        commandList.addAll(SparkParameters.SparkArgsUtils.buildArgs(this.getParameter(), defaultSparkAppName));
        return commandList;
    }

    @Override
    public String stepType() {
        return "spark";
    }

    @Override
    protected String fileExt() {
        return isPySpark() ? "py" : "jar";
    }

    /**
     * 判断是否为pySpark
     * @return
     */
    protected boolean isPySpark(){
        SparkParameters sparkParameters = this.getParameter();
        return sparkParameters != null && sparkParameters.getLanguageType() == SparkTaskType.PYTHON;
    }
}
