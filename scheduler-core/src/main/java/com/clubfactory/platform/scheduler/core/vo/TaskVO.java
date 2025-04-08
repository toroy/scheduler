package com.clubfactory.platform.scheduler.core.vo;

import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.dal.po.BaseJob;
import com.clubfactory.platform.scheduler.dal.po.Cluster;
import com.clubfactory.platform.scheduler.dal.po.JobType;
import com.clubfactory.platform.scheduler.dal.po.Task;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TaskVO extends Task {

    /**
     * 任务执行允许的超时时间：秒
     */
    private int timeout;

    /**
     *  env file
     **/
    private String envFile;

    /**
     * 用于判断是否为补数据：需要Master初始化,从ZK获取
     */
    private CommandType commandType;

    /**
     * 用于启动Task进程的用户:需要从DB关联获取
     */
    private String tenant;

    /**
     * 任务执行参数，需要从job表关联得到
     */
    private String params;

    /**
     * 需要根据create_user去从数据库关联查询
     */
    private String username;


    /**
     * 当前task对应的zNode路径
     */
    private String zNodePath;

    /**
     * 当前任务实例使用的脚本版本号,从ZK上获取
     */
    private  Integer version;

    /**
     * 脚本名称，从ZK上获取
     */
    private String fileName;


    /**
     * dfs上脚本存储的父目录
     */
    private String scriptBasePath;

    /**
     * 用于和airflow通信的参数(临时)
     */
    @Deprecated
    private String airflowTaskParam;

    /**
     * 用于标记Task是否已经执行完成
     */
    private boolean taskExit;

    /**
     * 日志地址
     */
    private String logUrl;

    /**
     * 是否使用临时集群运行,从Job表获取
     */
    private boolean runOnTmpEmr;


    /**
     * 原始脚本名称
     */
    private String rawScriptName;


    /**
     * 个性化Job配置信息
     */
    private Map<String,String> jobConf;

    /**
     * task对应的Job信息
     */
    private BaseJob baseJob;


    /**
     * 任务cluster信息
     */
    private Cluster cluster;

    /**
     * 全局宏变量列表
     */
    private List<MacroVarVO> globalParams;

    /**
     * 任务类型信息
     */
    private JobType jobTypeInfo;

    /**
     * 是否有阻塞型DQC子任务
     */
    private Boolean hasBlockDqcChildren;
}
