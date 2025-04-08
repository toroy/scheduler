CREATE TABLE sc_cluster
(
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    functions VARCHAR(256) COMMENT '功能，多个逗号分隔',
    proxy_user VARCHAR(256) COMMENT '执行用户名',
    proxy_password VARCHAR(256) COMMENT '执行用户密码',
    url VARCHAR(256) COMMENT '集群url',
    status VARCHAR(256) COMMENT '状态',
    depart_name VARCHAR(256) COMMENT '团队名称',
    depart_id bigint COMMENT '团队ID',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    cluster_name varchar(256) null COMMENT '集群名称',
    update_time timestamp null COMMENT '修改时间',
    yarn_rm_hosts varchar(256) COMMENT 'YARN RM hosts/ip，多个用英文逗号分隔' ,
    yarn_rm_http_port varchar(256) COMMENT 'YARN RM Http端口号',
    yarn_super_user varchar(256) COMMENT '用于杀任务的Yarn超级用户'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


