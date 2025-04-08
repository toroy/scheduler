CREATE TABLE sc_unsub_info
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    db_name VARCHAR(256) COMMENT '库名',
   sub_user_id BIGINT COMMENT '订阅人ID',
    data_source VARCHAR(256) COMMENT '数据源',
    table_name VARCHAR(256) COMMENT '表名',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


