package com.ckubfactory.platform.scheduler.task.param;

import com.clubfactory.platform.scheduler.spi.param.AbstractParameter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiejiajun
 */
@Data
public class DataXParameter extends AbstractParameter {

    private String rawScript;

    @Override
    public boolean checkParameters() {
        return !StringUtils.isAllBlank(this.startFile, this.rawScript);
    }
}
