package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.scheduler.core.enums.HttpCheckCondition;
import com.clubfactory.platform.scheduler.core.enums.HttpMethod;
import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author xiejiajun
 */
@Data
public class HttpParameters implements IParameters {


    /**
     * url
     */
    private String url;

    /**
     * httpMethod
     */
    private HttpMethod httpMethod;

    /**
     *  http params
     */
    private List<HttpProperty> httpParams;

    /**
     * httpCheckCondition
     */
    private HttpCheckCondition httpCheckCondition = HttpCheckCondition.STATUS_CODE_DEFAULT;

    /**
     * condition
     */
    private String condition;


    @Override
    public boolean checkParameters() {
        return  StringUtils.isNotEmpty(url);
    }

    /**
     * @param taskInfo
     * @param logger
     * @return
     */
    public static HttpParameters create(TaskVO taskInfo, Logger logger) {
        logger.info("http task params {}", taskInfo.getParams());
        HttpParameters httpParameters = JSONUtils.parseObject(taskInfo.getParams(), HttpParameters.class);
        if (httpParameters == null || !httpParameters.checkParameters()) {
            throw new RuntimeException("http task params is not valid");
        }
        return httpParameters;
    }
}
