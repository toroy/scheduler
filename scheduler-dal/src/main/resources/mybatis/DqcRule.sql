CREATE TABLE sc_dqc_rule
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    check_type VARCHAR(256) COMMENT '校验类型',
    compare VARCHAR(256) COMMENT '比较方式',
   upper_threshold BIGINT COMMENT '阈值上界',
    is_block VARCHAR(256) COMMENT '是否阻塞',
    db_name VARCHAR(256) COMMENT '库名',
    description VARCHAR(256) COMMENT '描述',
    expect_value VARCHAR(256) COMMENT '期望值',
    type VARCHAR(256) COMMENT '规则类型',
    sample VARCHAR(256) COMMENT '采样方式 countsumavg',
   user_id BIGINT COMMENT '责任人id',
    table_name VARCHAR(256) COMMENT '表名',
    filter VARCHAR(256) COMMENT '过滤条件',
   job_id BIGINT COMMENT '任务id',
   rel_job_id BIGINT COMMENT '关联任务id',
    field VARCHAR(256) COMMENT '规则字段',
    check_mode VARCHAR(256) COMMENT '校验方式',
    name VARCHAR(256) COMMENT '名称',
   dpc_partition_id BIGINT COMMENT '分区表达式id',
   lower_threshold BIGINT COMMENT '阈值下界',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


