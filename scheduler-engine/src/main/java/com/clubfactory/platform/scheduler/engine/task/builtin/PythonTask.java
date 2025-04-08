package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.PythonParameters;
import com.clubfactory.platform.scheduler.spi.launcher.PythonLauncher;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;

import static com.clubfactory.platform.scheduler.spi.launcher.PythonLauncher.PYTHON;
import static com.clubfactory.platform.scheduler.spi.launcher.PythonLauncher.getPythonCommand;


/**
 * @author xiejiajun
 */
public class PythonTask extends AbstractTask {

    public PythonTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return PythonParameters.class;
    }

    @Override
    protected void createLauncher() {
        super.createLauncher();
        PythonParameters pythonParameters = this.getParameter();
        if (StringUtils.isNotBlank(pythonParameters.getRawScript())) {
            this.taskLauncher = new PythonLauncher(this, this.logger, this.stateTracker);
        }
    }

    @Override
    protected String fileExt() {
        return "py";
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        List<String> commandList = Lists.newArrayList();
        PythonParameters pythonParameters = this.getParameter();
        if (pythonParameters.isScriptMode()) {
            String rawPythonScript = pythonParameters.getRawScript().replaceAll("\r\n", "\n");
            commandList.add(rawPythonScript);
        }else{
            if (StringUtils.isEmpty(pythonParameters.getStartFile())){
                throw new RuntimeException("python task params is not valid");
            }
            String mainArgs = pythonParameters.getMainArgs();
            commandList.add(getPyCommand());
            if (StringUtils.isNotBlank(pythonParameters.getSysConfigs())){
                commandList.addAll(pythonParameters.getSysConfigsList());
            }

            commandList.add(pythonParameters.getStartFile());
            if (StringUtils.isNotBlank(mainArgs)){
                commandList.add(mainArgs);
            }
        }
        return commandList;
    }

    /**
     * @return Python命令
     */
    private String getPyCommand() {
        String pythonCommand = this.getString(Constants.PYTHON_COMMAND);
        if (StringUtils.isBlank(pythonCommand)) {
            pythonCommand = getPythonCommand(taskInfo.getEnvFile());
        }
        if (StringUtils.isBlank(pythonCommand)){
            pythonCommand = PYTHON;
        }
        return pythonCommand + " -u";
    }
}
