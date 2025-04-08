package com.clubfactory.platform.scheduler.dal.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author xiejiajun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MacroVar {

    private Long id;

    /**
     * 变量名称
     */
    private String varName;

    /**
     * 变量表达式
     */
    private String varExpr;

    /**
     * 变量说明
     */
    private String varDesc;

    private Date createTime;

    private Date updateTime;

    /**
     * 是否需要脱敏
     */
    private Boolean isMask;

}
