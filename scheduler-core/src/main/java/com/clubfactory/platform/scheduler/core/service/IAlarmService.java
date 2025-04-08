package com.clubfactory.platform.scheduler.core.service;


public  interface IAlarmService   {

    /**
     * 初始化
     */
    void init();

    /**
     * worker停止告警
     * @param alertGroupId
     * @param host
     * @param serverType
     */
    void sendServerStoppedAlert(int alertGroupId,String host,String serverType);

}
