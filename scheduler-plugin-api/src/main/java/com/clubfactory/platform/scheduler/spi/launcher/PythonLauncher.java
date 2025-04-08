package com.clubfactory.platform.scheduler.spi.launcher;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.utils.FileUtils;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Python任务启动器
 * @author xiejiajun
 */
public class PythonLauncher extends AbstractLauncher{

    private static final Logger logger = LoggerFactory.getLogger(PythonLauncher.class);

    public static final String PYTHON = "python3";

    public PythonLauncher(AbstractTask task, Logger logger, AbstractTask.StateTracker stateTracker) {
        super(task, logger, stateTracker);
    }

    @Override
    protected String getExecutableFilePath() {
        return String.format("%s/%s.py", taskInfo.getExecuteDir(), taskName);
    }

    @Override
    protected List<String> launchCommand() {
        List<String> pythonLaunchCommand = Lists.newArrayList();
        String pythonCommand = getPythonCommand(taskInfo.getEnvFile());
        if (StringUtils.isBlank(pythonCommand)){
            pythonLaunchCommand.add(PYTHON);
        }else {
            pythonLaunchCommand.add(pythonCommand);
        }

        pythonLaunchCommand.add("-u");
        return pythonLaunchCommand;
    }

    @Override
    protected void createExecutableFileIfNotExists(String execCommand, String commandFile) throws IOException {
        logger.info("tenantCode :{}, task dir:{}", taskInfo.getTenant(), taskInfo.getExecuteDir());

        if (!Files.exists(Paths.get(commandFile))) {
            logger.info("generate command file:{}", commandFile);

            StringBuilder sb = new StringBuilder();
            sb.append("#-*- encoding=utf8 -*-\n");

            sb.append("\n\n");
            sb.append(execCommand);
            // write data to file
            FileUtils.writeStringToFile(new File(commandFile),
                    sb.toString(),
                    StandardCharsets.UTF_8);
        }
    }

    public static String getPythonCommand(String envPath){
        String pythonCommandConf = null;
        if (StringUtils.isNotEmpty(envPath)) {
            File envFile = new File(envPath);
            if (envFile.exists() && envFile.isFile()) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(envPath)))) {

                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains(Constants.PYTHON_COMMAND)) {
                            pythonCommandConf = line;
                            break;
                        }
                    }
                    if (org.apache.commons.lang.StringUtils.isEmpty(pythonCommandConf)) {
                        return null;
                    }
                    String[] arr = pythonCommandConf.split(Constants.EQUAL_SIGN);
                    if (arr.length == 2) {
                        return arr[1];
                    }

                } catch (IOException e) {
                    logger.error("read file failure", e);
                }
            }
        }
        return null;
    }
}
