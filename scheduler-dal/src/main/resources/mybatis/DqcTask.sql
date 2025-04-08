CREATE TABLE sc_dqc_task
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
   target_task_id BIGINT COMMENT '目标实例id',
    owner_name VARCHAR(256) COMMENT '拥有人',
    exception_num INT COMMENT '异常数',
    rule_num INT COMMENT '规则数',
    db_name VARCHAR(256) COMMENT '库名',
    exec_time timestamp COMMENT '执行时间',
    partition_value VARCHAR(256) COMMENT '分区数据',
    table_name VARCHAR(256) COMMENT '表名',
    task_time timestamp COMMENT '实例时间',
    status VARCHAR(256) COMMENT '状态',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


