CREATE TABLE sc_machine
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    slots INT COMMENT '槽位',
    functions VARCHAR(256) COMMENT '功能列表，逗号隔开',
    ip VARCHAR(256) COMMENT '主机ip',
    name VARCHAR(256) COMMENT '',
    type VARCHAR(256) COMMENT '机器功能类型',
    status VARCHAR(256) COMMENT '状态',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


