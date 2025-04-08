CREATE TABLE sc_user
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    uid VARCHAR(256) COMMENT '微信用户id',
    name VARCHAR(256) COMMENT '名字',
    depart_id INT COMMENT '部门id',
    is_admin VARCHAR(256) COMMENT '是否管理员',
    depart_name VARCHAR(256) COMMENT '部门名称',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


