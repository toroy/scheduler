CREATE TABLE sc_collect_db
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    ds_name VARCHAR(256) COMMENT '名称',
    feature VARCHAR(256) COMMENT '功能',
    ds_url VARCHAR(256) COMMENT '库url',
    db_name VARCHAR(256) COMMENT '数据库名称',
    db_host VARCHAR(256) COMMENT '数据库服务器host/ip',
    db_port VARCHAR(256) COMMENT '数据库服务Port',
    ds_type VARCHAR(256) COMMENT '类型',
    ds_password VARCHAR(1024) COMMENT '密码',
    ds_user VARCHAR(256) COMMENT '用户名',
    status VARCHAR(256) COMMENT '状态',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
    pwd_key     varchar(512)  COMMENT '密钥',
    encrypt_pwd varchar(1024) COMMENT '加密后的密码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


