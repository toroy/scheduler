package com.clubfactory.platform.scheduler.logger.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogVO {

    private int offset;

    private String content;
}
