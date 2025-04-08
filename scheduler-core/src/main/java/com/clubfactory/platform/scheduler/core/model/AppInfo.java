package com.clubfactory.platform.scheduler.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiejiajun
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {

    private String appId;

    private String appState;
}
