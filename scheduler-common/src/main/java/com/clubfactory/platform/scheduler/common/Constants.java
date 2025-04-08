package com.clubfactory.platform.scheduler.common;

import com.clubfactory.platform.scheduler.common.utils.OSUtils;

/**
 *
 * @author xiejiajun
 */
public interface Constants {


    /**
     * DFS相关配置信息
     */

    /**
     * fs.defaultFS
     */
    String FS_DEFAULT_FS = "fs.defaultFS";


    String SCHEDULER_SCRIPT_BASE_SCHEME                     = "scheduler.script.base.scheme";
//    String DEFAULT_SCHEDULER_SCRIPT_BASE_SCHEME             = "s3://cfdp-new";
    String DEFAULT_SCHEDULER_SCRIPT_BASE_SCHEME             = "cosn://gaia";

    /**
     * dfs configuration
     * dfs.super.user
     */
    String DFS_SUPER_USER = "dfs.super.user";

    /**
     * fs s3a endpoint
     * eg: s3.us-west-2.amazonaws.com
     */
    String FS_S3A_ENDPOINT = "fs.s3a.endpoint";


    /**
     * fs oss 相关
     */
    String FS_OSS_ENDPOINT = "fs.oss.endpoint";

    String FS_OSS_ACCESS_KEY_ID = "fs.oss.accessKeyId";

    String FS_OSS_ACCESS_KEY_SECRET = "fs.oss.accessKeySecret";

    String FS_OSS_SECURITY_TOKEN = "fs.oss.securityToken";

    String FS_OSS_MULTIPART_DOWNLOAD_SIZE = "fs.oss.multipart.download.size";

    /**
     * fs s3a access key
     */
    String FS_S3A_ACCESS_KEY = "fs.s3a.access.key";

    /**
     * fs s3a secret key
     */
    String FS_S3A_SECRET_KEY = "fs.s3a.secret.key";

    /**
     * s3 证书provider
     */
    String FS_S3A_CREDENTIALS_PROVIDER = "fs.s3a.aws.credentials.provider";

    /**
     * 默认s3证书provider
     */
    String FS_S3A_DEFAULT_CREDENTIALS_PROVIDER = "com.amazonaws.auth.DefaultAWSCredentialsProviderChain";


    /**
     * kerberos
     */
    String KERBEROS = "kerberos";

    /**
     * java.security.krb5.conf
     */
    String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";

    /**
     * java.security.krb5.conf.path
     */
    String JAVA_SECURITY_KRB5_CONF_PATH = "java.security.krb5.conf.path";

    /**
     * hadoop.security.authentication
     */
    String HADOOP_SECURITY_AUTHENTICATION = "hadoop.security.authentication";

    /**
     * hadoop.kerberos.authentication.enable
     */
    String HADOOP_KERBEROS_AUTHENTICATION_ENABLE = "hadoop.kerberos.authentication.enable";


    /**
     * loginUserFromKeytab user
     */
    String LOGIN_USER_KEY_TAB_USERNAME = "login.user.keytab.username";


    /**
     * loginUserFromKeytab path
     */
    String LOGIN_USER_KEY_TAB_PATH = "login.user.keytab.path";


    /**
     * Yarn相关配置信息
     */

    /**
     * yarn.rm.ha.ids
     */
    String YARN_RM_HA_HOSTS = "yarn.rm.ha.hosts";

    /**
     * yarn.rm.app.state.address
     */
    String YARN_RM_APP_STATE_ADDRESS = "yarn.rm.app.state.address";


    /**
     * 用于执行Yarn命令的超级用户
     */
    String YARN_SUPER_USER = "yarn.super.user";
    String YARN_DEFAULT_SUPER_USER = "hadoop";


    /**
     * yarn configuration
     */
    String YARN_RM_STATE_ACTIVE = "ACTIVE";

    String YARN_RM_STATE_STANDBY = "STANDBY";

    String YARN_RM_HTTP_PORT = "yarn.rm.http.port";


    /**
     * 资源存储相关配置
     */

    /**
     * dfs configuration
     * data.storage.dfs.base-path
     */
    String DATA_STORAGE_DFS_BASE_PATH = "data.storage.dfs.base-path";



    /**
     * resource.storage.dfs.type
     */
    String RESOURCE_STORAGE_DFS_TYPE = "resource.storage.dfs.type";


    /**
     * 任务执行相关配置
     */

    /**
     * process.exec.base-path
     */
    String PROCESS_EXEC_BASE_PATH = "process.exec.base-path";


    /**
     * 执行任务是否开启多租户模式
     */
    String PROCESS_EXEC_MULTI_TENANT_MODE_STARTUP_STATE = "process.exec.multi-tenant.mode.startup.state";

    /**
     * scheduler.env.path
     */
    String SCHEDULER_ENV_PATH = "scheduler.env.path";


    /**
     * python 命令全路径
     */
    String PYTHON_HOME = "PYTHON_HOME";


    /**
     * python 命令全路径
     */
    String PYTHON_COMMAND = "PYTHON_CMD";


    /**
     * 其他公共变量
     */

    /**
     * comma ,
     */
    String COMMA = ",";

    /**
     * COLON :
     */
    String COLON = ":";

    /**
     * SINGLE_SLASH /
     */
    String SINGLE_SLASH = "/";

    /**
     * DOUBLE_SLASH //
     */
    String DOUBLE_SLASH = "//";

    /**
     * SEMICOLON ;
     */
    String SEMICOLON = ";";

    /**
     * EQUAL SIGN
     */
    String EQUAL_SIGN = "=";

    /**
     * SINGLE_QUOTES
     */
    String SINGLE_QUOTES = "'";

    /**
     * DOUBLE_QUOTES
     */
    String DOUBLE_QUOTES = "\"";


    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * date format of yyyyMMddHHmmss
     */
    String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * http connect time out
     */
    int HTTP_CONNECT_TIMEOUT = 60 * 1000;


    /**
     * http connect request time out
     */
    int HTTP_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;

    /**
     * httpclient soceket time out
     */
    int SOCKET_TIMEOUT = 60 * 1000;


    /**
     * UTF-8
     */
    String UTF_8 = "UTF-8";

    /**
     * heartbeat threads number
     */
    int DEFAULT_WORKER_HEARTBEAT_THREAD_NUM = 5;

    /**
     * heartbeat interval
     */
    int DEFAULT_WORKER_HEARTBEAT_INTERVAL = 60;


    /**
     * default log cache rows num,output when reach the number
     */
    int DEFAULT_LOG_ROWS_NUM = 4 * 16;

    /**
     * log flush interval，output when reach the interval
     */
    int DEFAULT_LOG_FLUSH_INTERVAL = 1000;


    /***
     *
     * rpc port
     */
    int RPC_PORT = 50051;



    /**
     * sleep time
     */
    int SLEEP_TIME_MILLIS = 1000;

    /**
     * 数据库连接测试间隔
     */
    int DB_CONN_MONITOR_INTERVAL = 6 * 3600 * 1000;

    /**
     * slot刷新间隔：30分钟
     */
    int WORKER_SLOT_REFRESH_INTERVAL = 30 * 60_000;

    /**
     * config刷新间隔：30分钟
     */
    int WORKER_CONFIG_REFRESH_INTERVAL = 30 * 60_000;

    /**
     * system load monitor interval
     */
    int SYSTEM_MONITOR_INTERVAL = 60_000;


    String SYSTEM_MONITOR_ENABLE = "system.monitor.enable";

    /**
     * 半小时检测一下worker的slots数量，也就是从页面更新worker的slot数后，分钟后对应的worker会检测到（懒加载模式)
     */
    int SLOTS_CHECK_INTERVAL = 2 * 60 * 1000;

    /**
     * heartbeat for zk info length
     */
    int HEARTBEAT_FOR_ZOOKEEPER_INFO_LENGTH = 6;


    /**
     * spark params constant
     */
    String MASTER = "--master";

    String DEPLOY_MODE = "--deploy-mode";

    /**
     * --class CLASS_NAME
     */
    String CLASS = "--class";

    /**
     * --driver-cores NUM
     */
    String DRIVER_CORES = "--driver-cores";

    /**
     * --driver-memory MEM
     */
    String DRIVER_MEMORY = "--driver-memory";

    /**
     * --num-executors NUM
     */
    String NUM_EXECUTORS = "--num-executors";

    /**
     * --executor-cores NUM
     */
    String EXECUTOR_CORES = "--executor-cores";

    /**
     * --executor-memory MEM
     */
    String EXECUTOR_MEMORY = "--executor-memory";


    /**
     * --queue QUEUE
     */
    String SPARK_QUEUE = "--queue";


    /**
     * exit code success
     */
    int EXIT_CODE_SUCCESS = 0;

    /**
     * exit code kill
     */
    int EXIT_CODE_KILL = 137;

    /**
     * exit code failure
     */
    int EXIT_CODE_FAILURE = -1;

    /**
     * data verify failed
     */
    int EXIT_CODE_DATA_ILLEGAL = 255;

    /**
     * date format of yyyyMMdd
     */
    String PARAMETER_FORMAT_DATE = "yyyyMMdd";

    /**
     * date format of yyyy-MM-dd
     */
    String PARAMETER_FORMAT_DATE_MINUS = "yyyy-MM-dd";

    /**
     * date format of yyyyMMddHHmmss
     */
    String PARAMETER_FORMAT_TIME = "yyyyMMddHHmmss";

    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    String PARAMETER_FORMAT_TIME_DASH = "yyyy-MM-dd HH:mm:ss";

    /**
     * system date(yyyyMMddHHmmss)
     */
    String PARAMETER_DATETIME = "system.datetime";

    /**
     * system date(yyyymmdd) today
     */
    String PARAMETER_CURRENT_DATE = "system.biz.curdate";

    /**
     * system date(yyyy-MM-dd) yesterday
     */
    String PARAMETER_BUSINESS_DATE = "system.biz.date";


    /**
     * 实例时间
     * taskInstanceTime(yyyyMMddHHmmss)
     */
    String PARAMETER_INSTANCE_DATE = "task.instance.date";


    /**
     * 实例时间
     * taskInstanceTime(yyyy-MM-dd HH:mm:ss)
     */
    String PARAMETER_INSTANCE_DATE_CN = "task.instance.date.cn";

    /**
     * 当前系统日期：yyyy-MM-dd
     */
    String PARAMETER_SYS_DATE = "system.current.date";
    String CURRENT_DATE = "CURRENT_DATE";

    /**
     * 当前系统时间: yyyy-MM-dd HH:mm:ss
     */
    String PARAMETER_SYS_TIME = "system.current.time";
    String CURRENT_TIME = "CURRENT_TIME";

    /**
     * ACCEPTED
     */
    String ACCEPTED = "ACCEPTED";

    /**
     * SUCCEEDED
     */
    String SUCCEEDED = "SUCCEEDED";
    /**
     * NEW
     */
    String NEW = "NEW";
    /**
     * NEW_SAVING
     */
    String NEW_SAVING = "NEW_SAVING";
    /**
     * SUBMITTED
     */
    String SUBMITTED = "SUBMITTED";
    /**
     * FAILED
     */
    String FAILED = "FAILED";
    /**
     * KILLED
     */
    String KILLED = "KILLED";
    /**
     * RUNNING
     */
    String RUNNING = "RUNNING";

    /**
     * FINISHED
     */
    String FINISHED = "FINISHED";

    /**
     * http_err
     */
    String HTTP_ERR = "http_err";
    /**
     * underline  "_"
     */
    String UNDERLINE = "_";
    /**
     * application regex
     */
    String APPLICATION_REGEX = "application_\\d+_\\d+";
    String PID = "pid";
    /**
     * month_begin
     */
    String MONTH_BEGIN = "month_begin";
    /**
     * add_months
     */
    String ADD_MONTHS = "add_months";
    /**
     * month_end
     */
    String MONTH_END = "month_end";
    /**
     * week_begin
     */
    String WEEK_BEGIN = "week_begin";
    /**
     * week_end
     */
    String WEEK_END = "week_end";
    /**
     * timestamp
     */
    String TIMESTAMP = "timestamp";
    char SUBTRACT_CHAR = '-';
    char ADD_CHAR = '+';
    char MULTIPLY_CHAR = '*';
    char DIVISION_CHAR = '/';
    char LEFT_BRACE_CHAR = '(';
    char RIGHT_BRACE_CHAR = ')';
    String ADD_STRING = "+";
    String MULTIPLY_STRING = "*";
    String DIVISION_STRING = "/";
    String LEFT_BRACE_STRING = "(";
    char P = 'P';
    char N = 'N';
    String SUBTRACT_STRING = "-";
    String RWXR_XR_X = "rwxr-xr-x";


    /**
     * 系统资源使用控制相关
     */
    String WORKER_MAX_CPULOAD_AVG = "worker.max.cpu-load.avg";

    /**
     * 单位 GB
     */
    String WORKER_RESERVED_MEMORY = "worker.reserved.memory";

    /**
     * 单位：GB
     */
    String WORKER_TOTAL_MEMORY = "worker.total.memory.size";

    /**
     * worker cpu load
     */
    double DEFAULT_WORKER_CPU_LOAD = Runtime.getRuntime().availableProcessors() * 4.0 + 1.0;

    /**
     * worker reserved memory
     */
    double DEFAULT_WORKER_RESERVED_MEMORY = OSUtils.totalMemorySize() / 10;

    /**
     * 采集回流任务默认超时事件
     */
    String ETL_TASK_TIME_OUT = "etl.task.time-out";
    int DEFAULT_ETL_TASK_TIME_OUT = 3600;

    /**
     * 任务锁根路径
     */
    String TASK_LOCK_ROOT_PATH = "task.lock.root.path";
    String DEFAULT_TASK_LOCK_ROOT_PATH = "/platform/scheduler/task_locks";

    String WORKER_PROPERTIES_FILE = "worker.properties";
    String WORKER_CONFIG_DIR = "worker.config.dir";

    String SPARK_BIN ="spark.bin";


    String DATA_BEGIN_TIME_DELTA_HOURS = "data.begin.time.delta.hours";
    String DATA_END_TIME_DELTA_HOURS = "data.end.time.delta.hours";

    String ALLOW_MAX_LOG_BYTES = "allow.max.log.bytes";

    /**
     * 日志默认最大允许200M
     */
    long DEFAULT_ALLOW_MAX_LOG_BYTES = 200 * 1024 * 1024;


    /**
     * Job插件目录
     */
    String JOB_PLUGINS_DIR = "job.plugins.dir";

    /**
     * 实例时间基线使用中国时区
     */
    String  USE_BEIJING_TIMEZONE = "use-beijing-timezone";
    boolean DEFAULT_USE_BEIJING_TIMEZONE = false;
    String  BEIJING_TIMEZONE_ID = "GMT+8";

    /**
     * 实例时间基线时区ID
     */
    String BIZ_TIMEZONE_ID = "biz.timezone-id";

    /**
     * 时间表达式分隔符：用于分隔时间表达式和时区
     */
    String TIME_EXPR_DELIMITED = "#";

    /**
     * 是否使用启动时间作为时间宏变量基准
     */
    String TIME_BASIC_USE_START_TIME = "time.basic.use-start-time";
    boolean DEFAULT_TIME_BASIC_USE_START_TIME = false;

    /**
     * 流任务失败连续尝试重启的最大重试次数
     */
    String STREAMING_TASK_FAILOVER_RETRY_NUM = "streaming-task.failover.retry-num";
    Integer DEFAULT_STREAMING_TASK_FAILOVER_RETRY_NUM = 5;

    String STREAMING_TASK_QUEUE_KEY = "streaming-task.queue.key";
    String DEFAULT_STREAMING_TASK_QUEUE = "streaming";

    String FS_COS_ACCESS_KEY_ID = "fs.cos.accessKeyId";

    String FS_COS_ACCESS_KEY_SECRET = "fs.cos.accessKeySecret";

}
