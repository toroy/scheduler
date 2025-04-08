package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.JavaParameters;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejiajun
 */
public class JavaTask extends AbstractTask {

    /**
     *  java command
     */
    private static final String JAVA_CMD = "java";

    public JavaTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return JavaParameters.class;
    }

    @Override
    protected String fileExt() {
        return "jar";
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        JavaParameters javaParameters = this.getParameter();
        List<String> commandList = new ArrayList<>();
        commandList.add(JAVA_CMD);
        List<String> jvmArgs = javaParameters.getSysConfigsList();
        if (jvmArgs != null && jvmArgs.size() > 0){
            commandList.addAll(jvmArgs);
        }
        if (javaParameters.isJarMode()){
            commandList.add("-jar");
            commandList.add(javaParameters.getStartFile());
        }else {
            commandList.add("-cp");
            commandList.add(String.format("$(echo %s/*.jar | tr ' ' ':')",taskInfo.getExecuteDir()));
            commandList.add(javaParameters.getMainClass());
        }

        if (javaParameters.getMainArgs() != null){
            commandList.add(javaParameters.getMainArgs());
        }
        return commandList;
    }

}
