package com.clubfactory.platform.scheduler.spi.launcher;

import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author xiejiajun
 */
public class ShellLauncher extends AbstractLauncher{

    public static final String SH = "/bin/bash";

    public ShellLauncher(AbstractTask task, Logger logger, AbstractTask.StateTracker stateTracker) {
        super(task, logger, stateTracker);
    }

    @Override
    protected String getExecutableFilePath() {
        return String.format("%s/%s.sh", taskInfo.getExecuteDir(), taskName);
    }

    @Override
    protected List<String> launchCommand() {
        return Lists.newArrayList(SH);
    }

    @Override
    protected void createExecutableFileIfNotExists(String execCommand, String commandFile) throws IOException {
        logger.info("tenantCode user:{}, task dir:{}", taskInfo.getTenant(), taskInfo.getExecuteDir());
        String envFile = taskInfo.getEnvFile();
        // create if non existence
        if (!Files.exists(Paths.get(commandFile))) {
            logger.info("create command file:{}", commandFile);
            StringBuilder sb = new StringBuilder();
            sb.append("#!/bin/bash\n");
            sb.append("source /etc/profile\n");
            sb.append("source ~/.bashrc\n");
            if (envFile != null && new File(envFile).exists()) {
                sb.append("source ").append(envFile).append("\n");
            }
            sb.append("\n");
            sb.append("base_dir=$(cd `dirname $0`; pwd)\n");
            sb.append("cd $base_dir\n");
            sb.append("ppid=$$\n");
            sb.append("echo 1000 > /proc/${ppid}/oom_score_adj\n");
            sb.append("\n\n");
            sb.append(execCommand);
            // write data to file
            FileUtils.writeStringToFile(new File(commandFile), sb.toString(), StandardCharsets.UTF_8);
        }
    }
}
