CREATE TABLE sc_user_info
(
     id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    is_deleted Boolean COMMENT '是否删除，1：是，0：不是',
    im_robot VARCHAR(256) COMMENT '机器人',
    name VARCHAR(256) COMMENT '姓名',
   main_group_id BIGINT COMMENT '默认联系人组id',
   user_id BIGINT COMMENT '用户id',
   phone_no BIGINT COMMENT '手机号',
    email VARCHAR(256) COMMENT '邮箱地址',
    create_user bigint  COMMENT '创建人',
    update_user bigint  COMMENT '修改人',
    create_time timestamp null COMMENT '创建时间',
    update_time timestamp null COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


