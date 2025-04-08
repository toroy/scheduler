package com.clubfactory.platform.scheduler.spi.constants;

/**
 * @author xiejiajun
 */
public interface AwsEmrConstants {


    String AWS_EMRFS_METADATA_TABLE                         = "aws.emrfs.metadata-table";
    String DEFAULT_AWS_EMRFS_METADATA_TABLE                 = "EmrFSMetadata";

    /**
     * aws emr 版本
     */
    String AWS_EMR_RELEASE_LABLE                            = "aws.emr.release-label";
    String DEFAULT_AWS_EMR_RELEASE_LABLE                    = "emr-5.28.0";

    /**
     * aws emr集群日志存放位置
     */
    String AWS_EMR_LOG_URI                                  = "aws.emr.log.uri";
    String DEFAULT_AWS_EMR_LOG_URI                          = "s3://cfemrlog/tmpEmrLog/";

    String AWS_EMR_SERVICE_ROLE                             = "aws.emr.service-role";
    String DEFAULT_AWS_EMR_SERVICE_ROLE                     = "EMR_DefaultRole";

    String AWS_EMR_AUTO_SCALING_ROLE                        = "aws.emr.auto-scaling-role";
    String DEFAULT_AWS_EMR_AUTO_SCALING_ROLE                = "EMR_AutoScaling_DefaultRole";

    String AWS_EMR_JOB_FLOW_ROLE                            = "aws.emr.ec2-instance-profile";
    String DEFAULT_AWS_EMR_JOB_FLOW_ROLE                    = "EMR_EC2_DefaultRole";

    /**
     * 根分区大小:GB
     */
    String AWS_EMR_ROOT_VOLUME_SIZE                         = "aws.emr.root-volume.size";
    Integer DEFAULT_AWS_EMR_ROOT_VOLUME_SIZE                = 10;

    String AWS_EMR_TAG_KEY                                  = "aws.emr.tag.key";
    String DEFAULT_AWS_EMR_TAG_KEY                          = "cfdp";

    String AWS_EMR_EC2_SUBNET_ID                            = "aws.emr.ec2-subnet-id";
    String DEFAULT_AWS_EMR_EC2_SUBNET_ID                    = "subnet-dd9d94b9";

    String AWS_EMR_MASTER_SECURITY_GROUP                    = "aws.emr.master-security.group";
    String DEFAULT_AWS_EMR_MASTER_SECURITY_GROUP            = "sg-55e5ce2d";

    String AWS_EMR_CORE_SECURITY_GROUP                      = "aws.emr.core-security.group";
    String DEFAULT_AWS_EMR_CORE_SECURITY_GROUP              = "sg-56e5ce2e";

    String AWS_EMR_EC2_KEY_NAME                             = "aws.emr.ec2-key";
    String DEFAULT_AWS_EMR_EC2_KEY_NAME                     = "jinyang";

    String AWS_EMR_MASTER_INSTANCE_TYPE                     = "aws.emr.master-instance-type";
    String DEFAULT_AWS_EMR_MASTER_INSTANCE_TYPE             = "r4.8xlarge";


    String AWS_EMR_CORE_INSTANCE_TYPE                       = "aws.emr.core-instance-type";
    String DEFAULT_AWS_EMR_CORE_INSTANCE_TYPE               = "r4.8xlarge";

    String AWS_EMR_TASK_INSTANCE_TYPE                       = "aws.emr.core-instance-type";
    String DEFAULT_AWS_EMR_TASK_INSTANCE_TYPE               = "r4.8xlarge";


    String AWS_EMR_CORE_INSTANCE_COUNT                      = "aws.emr.core-instance-count";
    Integer DEFAULT_AWS_EMR_CORE_INSTANCE_COUNT             = 2;

    /**
     * 单位:GB
     */
    String AWS_EMR_CORE_INSTANCE_PER_DISK_SIZE              = "aws.emr.core.instance.per.disk-size";
    Integer DEFAULT_AWS_EMR_CORE_INSTANCE_PER_DISK_SIZE     = 256;


    String AWS_EMR_CORE_VOLUMES_PER_INSTANCE                = "aws.emr.core.volumes.per-instance";
    Integer DEFAULT_AWS_EMR_CORE_VOLUMES_PER_INSTANCE       = 1;


    String AWS_EMR_CORE_INSTANCE_MIN_CAPACITY               = "aws.emr.core.instance.min-capacity";
    Integer DEFAULT_AWS_EMR_CORE_INSTANCE_MIN_CAPACITY      = 0;

    String AWS_EMR_CORE_INSTANCE_MAX_CAPACITY               = "aws.emr.core.instance.max-capacity";
    Integer DEFAULT_AWS_EMR_CORE_INSTANCE_MAX_CAPACITY      = 6;



    /**
     * ON_DEMAND|SPOT
     */
    String AWS_EMR_WORKER_INSTANCE_MARKET_TYPE              = "aws.emr.worker.market-type";
    String DEFAULT_AWS_EMR_WORKER_INSTANCE_MARKET_TYPE      = "SPOT";


    String AWS_TEMP_CLUSTER_SCRIPT_BASE_DIR                 = "aws.temp.cluster.script.base.dir";
    String DEFAULT_AWS_TEMP_CLUSTER_SCRIPT_BASE_DIR         = "/data_platform/scheduler-exec-script/";

    String AWS_EMR_REGION                                   = "aws.emr.region";
    String DEFAULT_AWS_EMR_REGION                           = "us-west-2";

    /**
     * 单位:minutes
     */
    String AWS_EMR_CREATE_WAITING_TIMEOUT                   = "aws.emr.create.waiting.time-out";
    Integer DEFAULT_AWS_EMR_CREATE_WAITING_TIMEOUT          = 30;


    /**
     * 是否开启Task实例组(用于autoScaling)
     */
    String AWS_EMR_TASK_AUTO_SCALING_ENABLE                 = "aws.emr.task.auto.scaling.enable";
    boolean DEFAULT_AWS_EMR_TASK_AUTO_SCALING_ENABLE        = false;


    String FS_S3_CONSISTENT                                 = "fs.s3.consistent";
    String DEFAULT_FS_S3_CONSISTENT                         = "true";

    String AWS_EMR_TASK_DEBUG_ENABLE                        = "aws.emr.task.debug.enable";
    boolean DEFAULT_AWS_EMR_TASK_DEBUG_ENABLE               = false;


    String AWS_EMR_KEEP_ALIVE_ENABLE                        = "aws.emr.keep-alive.enable";
    boolean DEFAULT_AWS_EMR_KEEP_ALIVE_ENABLE               = false;



}

