CREATE TABLE sc_param
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    program_type VARCHAR(256) COMMENT '语言类型',
    default_value VARCHAR(256) COMMENT '默认值',
    name VARCHAR(256) COMMENT '字段名',
    job_type VARCHAR(256) COMMENT '任务类型',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


