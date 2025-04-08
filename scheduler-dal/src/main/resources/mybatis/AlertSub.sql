CREATE TABLE sc_alert_sub
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    db_name VARCHAR(256) COMMENT '库名',
    sub_type VARCHAR(256) COMMENT '订阅方式 依赖订阅，主动订阅',
    data_source VARCHAR(256) COMMENT '数据源',
   pic_id BIGINT COMMENT '表责任人ID 根据uid->user_id',
    table_name VARCHAR(256) COMMENT '表名',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


