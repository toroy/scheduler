package com.clubfactory.platform.scheduler.logger.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiejiajun
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DBConnVO {

    private Boolean status;

    private String errMsg;
}
