package com.zhugeio.platform.scheduler.server.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2025/9/10 23:22
 **/
@Data
public class ParamDto implements Serializable {

    private String name;

    private String value;
}
