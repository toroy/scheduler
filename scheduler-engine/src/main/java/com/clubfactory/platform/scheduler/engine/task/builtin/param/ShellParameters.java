package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;


/**
 * @author xiejiajun
 */
public class ShellParameters extends AbstractParameter {

    /**
     * shell script
     */
    @Getter @Setter
    private String rawScript;


    /**
     * 判断是脚本模式还是文件模式
     * 都不为空时优先script模式
     */
    @Getter
    private boolean scriptMode;

    @Override
    public boolean checkParameters() {
        this.scriptMode = StringUtils.isNotEmpty(rawScript);
        return StringUtils.isNotEmpty(rawScript) || StringUtils.isNotEmpty(startFile);
    }

    @Override
    public void normalizedParameter() {
        if (StringUtils.isBlank(rawScript)){
            this.startFile = this.taskContext.getFileName();
        }
    }


    /**
     * @param taskInfo
     * @param logger
     * @return
     */
    public static ShellParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("shell task params {}", taskInfo.getParams());
        ShellParameters shellParameters = JSONUtils.parseObject(taskInfo.getParams(), ShellParameters.class);
        if (shellParameters == null ) {
            throw new RuntimeException("shell task params is null");
        }
        if (StringUtils.isBlank(shellParameters.getRawScript())){
            shellParameters.setStartFile(taskInfo.getFileName());
        }
        if (!shellParameters.checkParameters()){
            throw new RuntimeException("shell task params is not valid");
        }
        return shellParameters;
    }


}
