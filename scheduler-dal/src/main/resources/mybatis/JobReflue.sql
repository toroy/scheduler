CREATE TABLE sc_job_reflue
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    target_table VARCHAR(256) COMMENT '目标表',
    sql_column VARCHAR(256) COMMENT 'sql字段',
    target_columns VARCHAR(256) COMMENT '配置字段',
    source_table VARCHAR(256) COMMENT '源表',
    increment_type VARCHAR(256) COMMENT '增量类型',
    where_sql VARCHAR(256) COMMENT 'where条件',
   db_source_id BIGINT COMMENT '源id',
    storage_format VARCHAR(256) COMMENT '存储格式',
    increment_column VARCHAR(256) COMMENT '增量字段名',
   db_target_id BIGINT COMMENT '目标id',
   job_id BIGINT COMMENT '任务id',
   serial_version_u_i_d BIGINT COMMENT '',
    is_sql VARCHAR(256) COMMENT '是否sql',
    user_sql VARCHAR(256) COMMENT '自定义sql',
    split_pk VARCHAR(256) COMMENT '分片字段',
    run_count INT COMMENT '并发数',
    source_columns VARCHAR(256) COMMENT '源表字段',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


