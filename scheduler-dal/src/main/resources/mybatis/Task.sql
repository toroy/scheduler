CREATE TABLE sc_task
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
   retry_count BIGINT COMMENT '当前重试次数',
    ip VARCHAR(256) COMMENT '机器ip',
    alarm_over_time timestamp COMMENT '告警超时时间',
   cluster_id BIGINT COMMENT '集群id',
    type VARCHAR(256) COMMENT '类型',
    priority INT COMMENT '优先级',
    dep_count INT COMMENT '依赖个数',
   job_id BIGINT COMMENT '任务id',
    score INT COMMENT '分值，通过job的level计算',
    alarm_status VARCHAR(256) COMMENT '告警状态',
   machine_id BIGINT COMMENT '调度机器id',
    log_path VARCHAR(256) COMMENT '日志地址',
    name VARCHAR(256) COMMENT '作业名称',
    start_time timestamp COMMENT '开始时间',
    task_time timestamp COMMENT '实例时间',
    exec_time timestamp COMMENT '执行时间',
    execute_dir VARCHAR(256) COMMENT '执行目录',
    end_time timestamp COMMENT '结束时间',
    category VARCHAR(256) COMMENT '类别',
    status VARCHAR(256) COMMENT '作业状态',
    depart_name VARCHAR(256) COMMENT '部门名称',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
    pid int(11) DEFAULT NULL COMMENT 'process id',
    emr_cluster_id varchar(100) COMMENT 'emr临时集群ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


