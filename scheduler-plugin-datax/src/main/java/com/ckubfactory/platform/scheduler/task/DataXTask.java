package com.ckubfactory.platform.scheduler.task;

import com.ckubfactory.platform.scheduler.task.param.DataXParameter;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.exception.TaskException;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

/**
 * @author xiejiajun
 */
public class DataXTask extends AbstractTask {
    private final static String DATAX_COMMAND_KEY = "datax.command";
    private final static String PYTHON_COMMAND_KEY = "datax.python.command";
    private final static String PYTHON = "python";
    
    public DataXTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return DataXParameter.class;
    }

    @Override
    protected String fileExt() {
        return "json";
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        String pythonCommand = this.getString(PYTHON_COMMAND_KEY, PYTHON);
        String dataXCommand = this.getString(DATAX_COMMAND_KEY);
        if (StringUtils.isBlank(dataXCommand)) {
            throw new TaskException("DataX任务启动命令未配置, 请联系管理员进行全局配置或通过datax.command系统配置进行指定");
        }
        List<String> commandList = Lists.newArrayList(pythonCommand, dataXCommand);
        DataXParameter dataXParameter = this.getParameter();
        if (StringUtils.isNotBlank(dataXParameter.getMainArgs())) {
            commandList.add(dataXParameter.getMainArgs());
        }
        if (StringUtils.isNotBlank(dataXParameter.getRawScript())) {
            this.writeScriptToFile(dataXParameter);
        }
        commandList.add(dataXParameter.getStartFile());
        return commandList;
    }


    /**
     * 将代码写到文件
     * @param dataXParameter
     * @throws IOException
     */
    private void writeScriptToFile(DataXParameter dataXParameter) throws IOException {
        String fileName = String.format("%s/%s", this.execDir, dataXParameter.getStartFile());
        Path path = new File(fileName).toPath();
        if (!Files.exists(path)) {
            String script = dataXParameter.getRawScript().replaceAll("\r\n", "\n");
            dataXParameter.setRawScript(script);
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString(Constants.RWXR_XR_X);
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

            Files.createFile(path, attr);
            Files.write(path, script.getBytes(), StandardOpenOption.APPEND);
        }
    }
}
