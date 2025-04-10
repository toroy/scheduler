package com.clubfactory.platform.scheduler.dal.po;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.clubfactory.platform.scheduler.common.bean.Pager;

import lombok.Data;

/**
 */
@Data
public class BasePO extends Pager {
    /**
     * 数据库主键
     */
    private Long id;

    /**
     * 是否已删除, 0:未删除, 1
     */
    private Boolean isDeleted;
    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    
    /**
     * 创建人
     */
    private Long createUser;
    
    private List<String> idsString;
    
    /**
     * 修改人
     */
    private Long updateUser;

    /**
     * 根据某个字段in查询
     */
    private String queryListFieldName;

    /**
     * 根据某个字段in查询
     */
    private List<Long> ids;

    /**
     * 参数map
     */
    private Map<String,Object> updateParam;
    
    /**
     * 排序字段，如 id asc
     */
    private String orderBy;

    /**
     * limit行数
     */
    private Integer limitRows;

    /**
     * 初始化创建参数
     */
    public void initCreate() {
        this.isDeleted = false;
        this.updateTime = this.createTime = new Date();
    }

    /**
     * 初始化更新参数
     */
    public void initUpdate() {
        this.updateTime = new Date();
    }

    /**
     * 初始化删除参数
     */
    public void initDelete() {
        this.isDeleted = false;
        this.updateTime = new Date();
    }
}
