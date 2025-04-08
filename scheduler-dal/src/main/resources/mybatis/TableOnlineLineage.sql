CREATE TABLE sc_table_online_lineage
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
   job_id BIGINT COMMENT '任务id',
    db_name VARCHAR(256) COMMENT '数据库名',
    db_type VARCHAR(256) COMMENT '数据库类型',
    db_host VARCHAR(256) COMMENT 'dbhost',
    type VARCHAR(256) COMMENT '',
   lineage_id BIGINT COMMENT '',
   parent_id BIGINT COMMENT '父任务id',
    table_name VARCHAR(256) COMMENT '表名',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


