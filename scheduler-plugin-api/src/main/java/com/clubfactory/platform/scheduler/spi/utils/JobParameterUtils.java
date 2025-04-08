package com.clubfactory.platform.scheduler.spi.utils;

import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import org.apache.commons.lang3.StringUtils;

import static com.clubfactory.platform.scheduler.common.Constants.SINGLE_QUOTES;

/**
 * @author xiejiajun
 */
public class JobParameterUtils {

    /**
     * ETL作业参数处理
     * @param etlJobParameter
     */
    public static void normalizedEtlParameter(AbstractParameter etlJobParameter) {
        String mainArgs = StringUtils.isBlank(etlJobParameter.getMainArgs()) ? null : etlJobParameter.getMainArgs();
        if (mainArgs == null) {
            etlJobParameter.setMainArgs(null);
            return;
        }
        if (!(mainArgs.startsWith(SINGLE_QUOTES) && mainArgs.endsWith(SINGLE_QUOTES))) {
            mainArgs = String.format("'%s'", mainArgs);
        }
        etlJobParameter.setMainArgs(mainArgs);
    }
}
