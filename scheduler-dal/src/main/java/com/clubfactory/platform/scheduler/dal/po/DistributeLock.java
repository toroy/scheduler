package com.clubfactory.platform.scheduler.dal.po;

import lombok.Data;

/**
 * @author xiejiajun
 */
@Data
public class DistributeLock {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 资源ID（唯一索引）
     */
    private Integer resourceId;

    /**
     * 资源描述
     */
    private String remark;

    /**
     * 锁过期时间：时间戳
     */
    private Long expireTime;

}
