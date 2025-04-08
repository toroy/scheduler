package com.clubfactory.platform.scheduler.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 *
 * @author zhoulijiang
 * @date 2021/5/14 3:02 下午
 **/

@RestController
public class HealthController {

    @GetMapping("/check-health")
    public String checkHealth() {
        return "success";
    }

}
