package com.clubfactory.platform.scheduler.dal.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
public class Team extends BasePO{

    /**
     * 部门ID
     */
    @JSONField(name = "id")
    private Integer departId;

    /**
     * 部门名称
     */
    @JSONField(name = "name")
    private String departName;
}
