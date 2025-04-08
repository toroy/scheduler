package com.clubfactory.platform.scheduler.engine.task.builtin.emr;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.ShellParameters;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractEmrTask;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;

import static com.clubfactory.platform.scheduler.spi.launcher.ShellLauncher.SH;


/**
 * @author xiejiajun
 */
public class EmrShellTask extends AbstractEmrTask {

    public EmrShellTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return ShellParameters.class;
    }

//    @Override
//    public void appendEmrConfig(List<Configuration> emrConfigs) {
//        Configuration hiveConfig = new Configuration()
//                .withClassification("hive-site")
//                .addPropertiesEntry("hive.metastore.client.factory.class",
//                        "com.amazonaws.glue.catalog.metastore.AWSGlueDataCatalogHiveClientFactory");
//        emrConfigs.add(hiveConfig);
//        Configuration sparkHiveConfig = new Configuration()
//                .withClassification("spark-hive-site")
//                .addPropertiesEntry("hive.metastore.client.factory.class",
//                        "com.amazonaws.glue.catalog.metastore.AWSGlueDataCatalogHiveClientFactory");
//        emrConfigs.add(sparkHiveConfig);
//    }
//
//    @Override
//    public List<Application> applications() {
//        List<Application> applications = Lists.newArrayList();
//        applications.add(new Application().withName("Ganglia"));
//        applications.add(new Application().withName("Hive"));
//        applications.add(new Application().withName("Spark"));
//        applications.add(new Application().withName("Flink"));
//        return applications;
//    }

    @Override
    public String stepType() {
        return "shell";
    }

    @Override
    protected String fileExt() {
        return "sh";
    }

    private List<String> processRawScript(){
        ShellParameters shellParameters = this.getParameter();
        String script = shellParameters.getRawScript().replaceAll("\r\n", "\n");
        shellParameters.setRawScript(script);
        return Lists.newArrayList(script);
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        ShellParameters shellParameters = this.getParameter();
        if (shellParameters.isScriptMode()){
            return processRawScript();
        }
        String startFile = shellParameters.getStartFile();
        String mainArgs = shellParameters.getMainArgs();
        List<String> commandList = Lists.newArrayList();
        commandList.add(SH);
        commandList.add(startFile);
        if (StringUtils.isNotBlank(mainArgs)) {
            commandList.add(mainArgs);
        }
        return commandList;
    }

}
