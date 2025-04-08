CREATE TABLE sc_dqc_partition
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    expression VARCHAR(256) COMMENT '分区表达式',
    db_name VARCHAR(256) COMMENT '库名',
    table_name VARCHAR(256) COMMENT '表名',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


