package com.clubfactory.platform.scheduler.server.config;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.enums.ResStorageFSType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author xiejiajun
 */
@Setter
@Component
public class CommonProperties {

    private final String OSS = "OSS";

    /**
     * 环境变量
     */
    @Value("${scheduler.env.path}")
    private String schedulerEnv;

    /**
     * task运行根目录
     */
    @Value("${process.exec.base-path}")
    private String processExecBaseDir;

    @Value("${process.exec.multi-tenant.mode.startup.state}")
    private String multiTenantModeState;


    /**
     * job资源存储相关
     */
    @Value("${resource.storage.dfs.type}")
    private String resStorageFSType;

    @Value("${dfs.super.user}")
    private String dfsSuperUser;

    @Value("${data.storage.dfs.base-path}")
    private String dfsStorageBaseDir;

    @Value("${fs.defaultFS}")
    private String defaultFS;

    @Value("${fs.s3a.endpoint}")
    private String s3aEndpoint;

    @Value("${fs.s3a.access.key}")
    private String s3aAccessKey;

    @Value("${fs.s3a.secret.key}")
    private String s3aSecretKey;

    @Value("${hadoop.kerberos.authentication.enable}")
    private String kerberosEnable;

    @Value("${java.security.krb5.conf.path}")
    private String krb5ConfPath;

    @Value("${login.user.keytab.username}")
    private String kerberosUser;

    @Value("${login.user.keytab.path}")
    private String keytabPath;


    @Value("${fs.oss.endpoint}")
    private String ossEndpoint;

    @Value("${fs.oss.access-key-id}")
    private String ossAccessKeyId;

    @Value("${fs.oss.access-key-secret}")
    private String ossAccessKeySecret;

    @Value("${fs.oss.security-token}")
    private String ossStsToken;

    @Value("${fs.oss.multipart.download.size}")
    private String ossMultipartDownloadSize;



    /**
     * Yarn任务状态查询相关
     */

    /**
     * 这里的ids直接配置逗号分隔的host列表
     */
    @Value("${yarn.rm.ha.hosts}")
    private String rmHaHosts;

    @Value("${yarn.rm.http.port}")
    private String rmHttpPort;

    @Getter
    @Value("${yarn.super.user}")
    private String yarnSuperUser;


    /**
     * s3a 内部http连接池大小设置：默认15
     */
    @Value("${fs.s3a.connection.maximum}")
    private String s3aConnPoolSize;

    /**
     * s3a 执行命令失败最大重试次数：默认20
     */
    @Value("${fs.s3a.attempts.maximum}")
    private String s3aMaxAttempts;

    /**
     * s3a 建立连接超时时间：默认5000ms
     */
    @Value("${fs.s3a.connection.establish.timeout}")
    private String s3aConnEstablishTimeout;

    /**
     * s3a Socket连接超时时间：默认200000ms
     */
    @Value("${fs.s3a.connection.timeout}")
    private String s3aConnTimeout;

    /**
     * s3a 用于分片并发上传的线程数（这些线程也算在s3a的连接池中）：默认10
     */
    @Value("${fs.s3a.threads.max}")
    private String concurrentUploadThreads;

    /**
     * s3a 线程池中空闲线程等待终止时间,空闲时间超过该值的线程将被关闭：默认60s
     */
    @Value("${fs.s3a.threads.keepalivetime}")
    private String threadKeepAliveTime;


    /**
     * 允许的系统最大CPU负载,double类型
     */
    @Value("${worker.max.cpu-load.avg}")
    private String workerMaxCpuLoadAvg;

    /**
     * 系统预留内存配置，当预留内存小于该值时暂停调度任务，double类型
     */
    @Value("${worker.reserved.memory}")
    private String workerSysReservedMem;


    @Value("${etl.task.time-out}")
    private String etlTaskTimeout;

    @Value("${zookeeper.scheduler.task.lock.root-path}")
    private String taskLockRootPath;
    @Value("${fs.cos.access-key-id}")
    private String cosAccessKeyId;

    @Value("${fs.cos.access-key-secret}")
    private String cosAccessKeySecret;


    public String getS3aConnPoolSize() {
        if (StringUtils.isBlank(s3aConnPoolSize)){
            s3aConnPoolSize = "15";
        }
        return s3aConnPoolSize;
    }

    public String getS3aMaxAttempts() {
        if (StringUtils.isBlank(s3aMaxAttempts)){
            s3aMaxAttempts = "20";
        }
        return s3aMaxAttempts;
    }

    public String getS3aConnEstablishTimeout() {
        if (StringUtils.isBlank(s3aConnEstablishTimeout)){
            s3aConnEstablishTimeout = "5000";
        }
        return s3aConnEstablishTimeout;
    }

    public String getS3aConnTimeout() {
        if (StringUtils.isBlank(s3aConnTimeout)){
            s3aConnTimeout = "200000";
        }
        return s3aConnTimeout;
    }

    public String getConcurrentUploadThreads() {
        if (StringUtils.isBlank(concurrentUploadThreads)){
            concurrentUploadThreads = "10";
        }
        return concurrentUploadThreads;
    }

    public String getThreadKeepAliveTime() {
        if (StringUtils.isBlank(threadKeepAliveTime)){
            threadKeepAliveTime = "60";
        }
        return threadKeepAliveTime;
    }

    public String getSchedulerEnv() {
        return schedulerEnv;
    }


    public String getProcessExecBaseDir() {
        return processExecBaseDir;
    }

    public String getMultiTenantModeState(){
        if (StringUtils.isBlank(multiTenantModeState) ||
                        !multiTenantModeState.equalsIgnoreCase("true")){
            multiTenantModeState = "false";
        }
        return multiTenantModeState.toLowerCase();

    }


    public String getResStorageFSType() {
        if (StringUtils.isEmpty(resStorageFSType)){
            resStorageFSType = ResStorageFSType.HDFS.name();
        }
        return resStorageFSType;
    }

    public String getDfsSuperUser() {
        if (StringUtils.isEmpty(dfsSuperUser)){
            dfsSuperUser = "hdfs";
        }
        return dfsSuperUser;
    }

    public String getDfsStorageBaseDir() {
        return dfsStorageBaseDir;
    }

    public String getDefaultFS() {
        return defaultFS;
    }

    public String getS3aEndpoint() {
        return s3aEndpoint;
    }

    public String getS3aAccessKey() {
        return s3aAccessKey;
    }

    public String getS3aSecretKey() {
        return s3aSecretKey;
    }

    public String getKerberosEnable() {
        if (StringUtils.isEmpty(kerberosEnable)){
            kerberosEnable = "false";
        }
        return kerberosEnable;
    }

    public String getKrb5ConfPath() {
        return krb5ConfPath;
    }

    public String getKerberosUser() {
        return kerberosUser;
    }

    public String getKeytabPath() {
        return keytabPath;
    }

    public String getRmHaHosts() {
        return rmHaHosts;
    }

    public String getRmHttpPort() {
        if (StringUtils.isEmpty(rmHttpPort)){
            rmHttpPort = "8088";
        }
        return rmHttpPort;
    }

    public String getWorkerMaxCpuLoadAvg() {
        return workerMaxCpuLoadAvg;
    }

    public String getWorkerSysReservedMem() {
        return workerSysReservedMem;
    }

    public String getOssEndpoint() {
        if (StringUtils.isBlank(ossEndpoint) && OSS.equals(getResStorageFSType())){
            throw new RuntimeException("oss endpoint 未配置");
        }
        return ossEndpoint;
    }

    public String getOssAccessKeyId() {
        if (StringUtils.isBlank(ossAccessKeyId) && OSS.equals(getResStorageFSType())){
            throw new RuntimeException("oss accessKeyId 未配置");
        }
        return ossAccessKeyId;
    }

    public String getOssAccessKeySecret() {
        if (StringUtils.isBlank(ossAccessKeySecret) && OSS.equals(getResStorageFSType())){
            throw new RuntimeException("oss accessKeySecret 未配置");
        }
        return ossAccessKeySecret;
    }

    public String getOssStsToken() {
        return ossStsToken;
    }

    public String getOssMultipartDownloadSize() {
        return ossMultipartDownloadSize;
    }

    public String getEtlTaskTimeout() {
        return etlTaskTimeout;
    }

    private String getTaskLockRootPath(){
        if (StringUtils.isBlank(this.taskLockRootPath)){
            this.taskLockRootPath = Constants.DEFAULT_TASK_LOCK_ROOT_PATH;
        }
        return this.taskLockRootPath;
    }

    /**
     * 将已有配置转换成Properties
     * @return
     */
    public Properties getProperties(){
        Properties props = new Properties();

        props.put(Constants.SCHEDULER_ENV_PATH,this.getSchedulerEnv());
        props.put(Constants.PROCESS_EXEC_BASE_PATH,this.getProcessExecBaseDir());
        props.put(Constants.RESOURCE_STORAGE_DFS_TYPE,getResStorageFSType());
        props.put(Constants.DFS_SUPER_USER,getDfsSuperUser());
        props.put(Constants.DATA_STORAGE_DFS_BASE_PATH,getDfsStorageBaseDir());
        props.put(Constants.FS_DEFAULT_FS,getDefaultFS());
        props.put(Constants.FS_S3A_ENDPOINT,getS3aEndpoint());
        props.put(Constants.FS_S3A_ACCESS_KEY,getS3aAccessKey());
        props.put(Constants.FS_S3A_SECRET_KEY,getS3aSecretKey());
        props.put(Constants.HADOOP_KERBEROS_AUTHENTICATION_ENABLE,getKerberosEnable());
        props.put(Constants.JAVA_SECURITY_KRB5_CONF_PATH,getKrb5ConfPath());
        props.put(Constants.LOGIN_USER_KEY_TAB_USERNAME,getKerberosUser());
        props.put(Constants.LOGIN_USER_KEY_TAB_PATH,getKeytabPath());
        props.put(Constants.YARN_RM_HA_HOSTS,getRmHaHosts());
        props.put(Constants.YARN_RM_HTTP_PORT,getRmHttpPort());
        props.put(Constants.YARN_SUPER_USER,getYarnSuperUser());
        props.put(Constants.PROCESS_EXEC_MULTI_TENANT_MODE_STARTUP_STATE,getMultiTenantModeState());

        props.put("fs.s3a.connection.maximum",getS3aConnPoolSize());
        props.put("fs.s3a.attempts.maximum",getS3aMaxAttempts());
        props.put("fs.s3a.connection.establish.timeout",getS3aConnEstablishTimeout());
        props.put("fs.s3a.connection.timeout",getS3aConnTimeout());
        props.put("fs.s3a.threads.max",getConcurrentUploadThreads());
        props.put("fs.s3a.threads.keepalivetime",getThreadKeepAliveTime());

        props.put(Constants.WORKER_MAX_CPULOAD_AVG,getWorkerMaxCpuLoadAvg());
        props.put(Constants.WORKER_RESERVED_MEMORY,getWorkerSysReservedMem());

        props.put(Constants.FS_OSS_ACCESS_KEY_ID,getOssAccessKeyId());
        props.put(Constants.FS_OSS_ACCESS_KEY_SECRET,getOssAccessKeySecret());
        props.put(Constants.FS_OSS_ENDPOINT,getOssEndpoint());
        props.put(Constants.FS_OSS_SECURITY_TOKEN,getOssStsToken());
        props.put(Constants.FS_OSS_MULTIPART_DOWNLOAD_SIZE,getOssMultipartDownloadSize());

        props.put(Constants.ETL_TASK_TIME_OUT,getEtlTaskTimeout());

        props.put(Constants.TASK_LOCK_ROOT_PATH,getTaskLockRootPath());

        props.put(Constants.FS_COS_ACCESS_KEY_ID,cosAccessKeyId);
        props.put(Constants.FS_COS_ACCESS_KEY_SECRET,cosAccessKeySecret);

        return props;
    }
}
