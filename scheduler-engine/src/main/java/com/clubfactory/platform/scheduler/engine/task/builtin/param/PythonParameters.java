package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;


/**
 * @author xiejiajun
 */
@Data
public class PythonParameters extends AbstractParameter {

    /**
     * python源代码
     */
    private String rawScript;

    /**
     * 用于判断taskInstance传递的是python源码还是指定了启动文件
     * 源码优先
     */
    private boolean scriptMode;


    @Override
    public boolean checkParameters() {
        // rawScript不为空则认为是源码模式
        scriptMode = StringUtils.isNotBlank(rawScript);
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
    public static PythonParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("python task params {}", taskInfo.getParams());
        PythonParameters pythonParameters = JSONUtils.parseObject(taskInfo.getParams(), PythonParameters.class);
        if (pythonParameters == null) {
            throw new RuntimeException("python task params is null");
        }
        if (StringUtils.isBlank(pythonParameters.getRawScript())){
            pythonParameters.setStartFile(taskInfo.getFileName());
        }
        if (!pythonParameters.checkParameters()){
            throw new RuntimeException("python task params is not valid");
        }
        return pythonParameters;
    }

}
