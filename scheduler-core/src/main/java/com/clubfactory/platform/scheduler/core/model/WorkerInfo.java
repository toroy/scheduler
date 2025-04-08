package com.clubfactory.platform.scheduler.core.model;

import lombok.Data;

@Data
public class WorkerInfo {

    private int id;

    private String ip;

    private String functions;

    private String status;

    private int slots;

    private String proxyUser;
}
