CREATE TABLE sc_script
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    file_name VARCHAR(256) COMMENT '任务资源名称:UUID',
    remark VARCHAR(256) COMMENT '描述',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间',
    script_type VARCHAR(256) COMMENT '脚本类型',
    version int not null default 1 COMMENT '版本号',
    file_ext varchar(256) comment '文件扩展名',
    script_name varchar(256) comment '用户指定的文件名称，只用于展示',
    script_base_path varchar(1024) comment '脚本存储父目录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


