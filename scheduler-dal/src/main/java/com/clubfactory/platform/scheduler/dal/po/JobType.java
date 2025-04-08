package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.JobCategoryEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author xiejiajun
 */
@Data
public class JobType {

    private Long id;

    private String pluginName;

    /**
     * 用于页面展示的任务别名
     */
    private String pluginAlias;

    private String pluginDesc;

    private String pluginClazz;

    private String pluginEmrClazz;

    private Date createTime;

    private Date updateTime;

    private String pluginDir;

    /**
     * 是否为流任务
     */
    private Boolean isStream;

    /**
     * 是否为需要提交到集群的任务
     */
    private Boolean isClusterJob;

    /**
     * 是否支持血缘解析
     */
    private Boolean isSupportLineage;

    /**
     * 任务分类：添加任务时的辅助筛选条件
     */
    private JobCategoryEnum category;

    public boolean isValid() {
        return StringUtils.isNotBlank(pluginName) &&
                (StringUtils.isNotBlank(pluginClazz) || StringUtils.isNotBlank(pluginEmrClazz));
    }

    public boolean isDataCrawler() {
        return category != null && category == JobCategoryEnum.COLLECT;
    }

    public boolean isDataPush() {
        return category != null && category == JobCategoryEnum.REFLUE;
    }

    public boolean isCal() {
        return category != null && category == JobCategoryEnum.CAL;
    }

    /**
     * 用于页面展示的任务类型
     * @return
     */
    public String getType() {
        return String.format("%s|%s", category.getDesc(), pluginName);
    }

    /**
     * 获取集群/调度机功能
     * @return
     */
    public String getFunction() {
        return String.format("%s_%s", category.name(), pluginName);
    }
}
