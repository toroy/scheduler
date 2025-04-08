package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

@Data
public class Project extends BasePO {

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 描述
     */
    private String description;

}
