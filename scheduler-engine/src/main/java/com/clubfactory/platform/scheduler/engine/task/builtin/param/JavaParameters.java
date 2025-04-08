package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;


/**
 * @author xiejiajun
 */
@Data
public class JavaParameters extends AbstractParameter {

    /**
     * 主方法
     */
    private String mainClass;

    /**
     * 判断是以java -jar还是java -cp 运行jar包
     */
    private boolean jarMode;

    @Override
    public boolean checkParameters() {
        this.jarMode = StringUtils.isBlank(this.mainClass);
        return StringUtils.isNotBlank(this.startFile) || !this.jarMode;
    }

    /**
     * @param taskInfo
     * @param logger
     * @return
     */
    public static JavaParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("java task params {}", taskInfo.getParams());
        JavaParameters javaParameters = JSONUtils.parseObject(taskInfo.getParams(), JavaParameters.class);
        if (javaParameters == null) {
            throw new RuntimeException("java task params is null");
        }
        javaParameters.setStartFile(taskInfo.getFileName());
        if (!javaParameters.checkParameters()){
            throw new RuntimeException("java task params is invalid");
        }
        return javaParameters;
    }

}
