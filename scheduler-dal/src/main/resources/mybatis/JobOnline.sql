CREATE TABLE sc_job_online
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    level INT COMMENT '层级',
    categroy VARCHAR(256) COMMENT '',
   cluster_id BIGINT COMMENT '集群id',
    type VARCHAR(256) COMMENT '类型',
    priority INT COMMENT '优先级',
    params VARCHAR(256) COMMENT '其它参数',
    version INT COMMENT '版本号',
    retry_max INT COMMENT '重试次数限制',
    cycle_type VARCHAR(256) COMMENT '周期类型',
   check_user BIGINT COMMENT '审核人',
   script_id BIGINT COMMENT '脚本id',
   machine_id BIGINT COMMENT '调度机id',
    check_time timestamp COMMENT '审核时间',
    retry_dur INT COMMENT '重试间隔（分钟）',
    scheduler_time VARCHAR(256) COMMENT '调度时间',
    name VARCHAR(256) COMMENT '任务名',
    exec_param VARCHAR(256) COMMENT '直接用来执行，组装好的参数',
    depart_name VARCHAR(256) COMMENT '部门名称',
    status VARCHAR(256) COMMENT '任务状态',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
    run_on_tmp_emr Boolean COMMENT '是否使用临时集群运行，1：是，0：不是',
    job_conf   VARCHAR(512) COMMENT '作业级别的个性化配置',
    job_type_id BIGINT COMMENT '作业类型id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


