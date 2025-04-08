package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.ShellParameters;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import com.clubfactory.platform.scheduler.spi.utils.ProcessUtils;
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

import static com.clubfactory.platform.scheduler.spi.launcher.ShellLauncher.SH;


/**
 * @author xiejiajun
 */
public class ShellTask extends AbstractTask {

    public ShellTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return ShellParameters.class;
    }

    @Override
    protected String fileExt() {
        return "sh";
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

    protected List<String> processRawScript() throws IOException {
        List<String> commandList = Lists.newArrayList();
        ShellParameters shellParameters = this.getParameter();
        String fileName = String.format("%s/%s_node.sh", this.execDir, this.taskName);
        Path path = new File(fileName).toPath();
        if (!Files.exists(path)) {
            String script = shellParameters.getRawScript().replaceAll("\r\n", "\n");
            shellParameters.setRawScript(script);
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString(Constants.RWXR_XR_X);
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

            Files.createFile(path, attr);
            Files.write(path, shellParameters.getRawScript().getBytes(), StandardOpenOption.APPEND);
        }
        commandList.add(SH);
        commandList.add(fileName);
        return commandList;
    }

    @Override
    public int ensureState(int exitStatusCode) {
        // block until yarn application finish
        return ProcessUtils.ensureYarnState(this.taskInfo,this.logger,exitStatusCode);
    }
}
