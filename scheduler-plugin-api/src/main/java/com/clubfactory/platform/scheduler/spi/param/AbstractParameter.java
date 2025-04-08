package com.clubfactory.platform.scheduler.spi.param;

import com.clubfactory.platform.scheduler.common.ParamConstants;
import com.clubfactory.platform.scheduler.common.utils.CliParamSplitter;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xiejiajun
 */
@Data
public abstract class AbstractParameter implements IParameters {

    /**
     * jar包文件名
     */
    protected String startFile;

    /**
     * main方法的入参，或者是hivevar参数
     */
    protected String mainArgs;

    /**
     * 其他参数(用于修改python路径、emr临时集群key等信息的盖亚级别配置参数)
     */
    protected String params;

    /**
     * 系统参数，或者hiveconf参数
     */
    protected String sysConfigs;

    /**
     * Task任务上下文信息
     */
    protected TaskVO taskContext;


    @Override
    public void normalizedParameter() {
    }

    /**
     * 获取系统配置列表
     * @return
     */
    public List<String> getSysConfigsList() {
        if (StringUtils.isBlank(this.sysConfigs)) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(this.sysConfigs.split(ParamConstants.GAIA_JOB_PARAM_DELIMITED));
    }

    /**
     * 获取mainArgs列表
     * @return
     */
    public List<String> getMainArgsList() {
        return CliParamSplitter.splitParams(this.mainArgs);
    }

    /**
     * 组装系统参数
     * @param argList
     */
    public void buildSysConfig(List<String> argList) {
        if (CollectionUtils.isEmpty(this.getSysConfigsList())) {
            return;
        }
        for (String config : this.getSysConfigsList()) {
            if (StringUtils.isBlank(config)) {
                continue;
            }
            argList.add(config);
            argList.add("\\\n");
        }
    }

}
